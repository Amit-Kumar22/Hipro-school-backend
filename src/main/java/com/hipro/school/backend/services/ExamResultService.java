package com.hipro.school.backend.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hipro.school.backend.dtos.ExamResultFilterRequest;
import com.hipro.school.backend.dtos.ExamResultRequest;
import com.hipro.school.backend.dtos.ExamResultResponse;
import com.hipro.school.backend.entities.Exam;
import com.hipro.school.backend.entities.ExamResult;
import com.hipro.school.backend.entities.ExamSubject;
import com.hipro.school.backend.entities.School;
import com.hipro.school.backend.entities.Student;
import com.hipro.school.backend.enums.ResultStatus;
import com.hipro.school.backend.exceptions.ResourceNotFoundException;
import com.hipro.school.backend.exceptions.ServiceException;
import com.hipro.school.backend.repositories.ExamRepository;
import com.hipro.school.backend.repositories.ExamResultRepository;
import com.hipro.school.backend.repositories.ExamSubjectRepository;
import com.hipro.school.backend.repositories.SchoolRepository;
import com.hipro.school.backend.repositories.StudentRepository;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExamResultService {

	private final ExamResultRepository examResultRepository;
	private final ExamRepository examRepository;
	private final ExamSubjectRepository examSubjectRepository;
	private final StudentRepository studentRepository;
	private final SchoolRepository schoolRepository;
	private final ExamResultPdfService examResultPdfService;

	@Transactional
	public ExamResultResponse create(ExamResultRequest request) {
		Exam exam = getExam(request.getExamId());
		ExamSubject examSubject = getExamSubject(request.getExamSubjectId());
		Student student = getStudent(request.getStudentId());
		validateExamSubjectBelongsToExam(exam, examSubject);
		validateStudentBelongsToExamClass(exam, student);
		if (examResultRepository.existsByExamIdAndExamSubjectIdAndStudentId(exam.getId(), examSubject.getId(),
				student.getId())) {
			throw new IllegalArgumentException("Result already exists for this student and subject");
		}

		ExamResult result = new ExamResult();
		result.setExam(exam);
		result.setExamSubject(examSubject);
		result.setStudent(student);
		result.setGrade(request.getGrade());
		setResultValues(result, examSubject, request);
		ExamResult saved = examResultRepository.save(result);
		return mapToResponse(saved);
	}

	@Transactional
	public ExamResultResponse update(Long id, ExamResultRequest request) {
		ExamResult result = getResult(id);
		Exam exam = getExam(request.getExamId());
		ExamSubject examSubject = getExamSubject(request.getExamSubjectId());
		Student student = getStudent(request.getStudentId());
		validateExamSubjectBelongsToExam(exam, examSubject);
		validateStudentBelongsToExamClass(exam, student);

		// Don't allow changing the unique combination to
		// an already existing result.
		if (!result.getExam().getId().equals(exam.getId())
				|| !result.getExamSubject().getId().equals(examSubject.getId())
				|| !result.getStudent().getId().equals(student.getId())) {

			boolean exists = examResultRepository.existsByExamIdAndExamSubjectIdAndStudentId(exam.getId(),
					examSubject.getId(), student.getId());

			if (exists) {
				throw new IllegalArgumentException("Result already exists for this student and subject");
			}
		}

		result.setExam(exam);
		result.setExamSubject(examSubject);
		result.setStudent(student);
		setResultValues(result, examSubject, request);
		ExamResult updated = examResultRepository.save(result);
		return mapToResponse(updated);
	}

	public ExamResultResponse getById(Long id) {
		return mapToResponse(getResult(id));
	}

	public List<ExamResultResponse> getAll() {
		return examResultRepository.findAll().stream().map(this::mapToResponse).toList();
	}

	@Transactional(readOnly = true)
	public byte[] generateResultsPdf(Long studentId) {
		log.info("Generating exam results PDF for studentId: {}", studentId);
		if (studentId != null && !studentRepository.existsById(studentId)) {
			throw new ResourceNotFoundException("Student not found: " + studentId);
		}
		List<ExamResult> results = studentId != null ? examResultRepository.findForPdfByStudentId(studentId)
				: examResultRepository.findAllForPdf();
		if (results.isEmpty()) {
			throw new ServiceException(
					studentId != null ? "No exam results found for student: " + studentId : "No exam results found");
		}
		School school = schoolRepository.findAll().stream().findFirst()
				.orElseThrow(() -> new ResourceNotFoundException("School not found. Please configure school details first."));
		return examResultPdfService.generateResultsPdf(results, school);
	}

	@Transactional
	public void delete(Long id) {
		ExamResult result = getResult(id);
		if (result.getStatus() == ResultStatus.PUBLISHED) {
			throw new IllegalStateException("Published result cannot be deleted");
		}
		examResultRepository.delete(result);
	}

	@Transactional
	public ExamResultResponse updateStatus(Long id, ResultStatus status) {
		ExamResult result = getResult(id);
		validateStatusTransition(result.getStatus(), status);
		result.setStatus(status);
		return mapToResponse(examResultRepository.save(result));
	}

	private void setResultValues(ExamResult result, ExamSubject examSubject, ExamResultRequest request) {
		BigDecimal totalMarks = examSubject.getTotalMarks();
		BigDecimal passingMarks = examSubject.getPassingMarks();
		result.setTotalMarks(totalMarks);
		result.setPassingMarks(passingMarks);
		boolean absent = Boolean.TRUE.equals(request.getAbsent());
		result.setAbsent(absent);
		if (absent) {

			result.setObtainedMarks(null);
			result.setPercentage(null);
			result.setGrade(null);
			result.setPassed(false);

		} else {

			BigDecimal obtainedMarks = request.getObtainedMarks();

			if (obtainedMarks == null) {
				throw new IllegalArgumentException("Obtained marks are required for a present student");
			}

			if (obtainedMarks.compareTo(BigDecimal.ZERO) < 0) {
				throw new IllegalArgumentException("Obtained marks cannot be negative");
			}

			if (obtainedMarks.compareTo(totalMarks) > 0) {
				throw new IllegalArgumentException("Obtained marks cannot be greater than total marks");
			}

			result.setObtainedMarks(obtainedMarks);

			BigDecimal percentage = obtainedMarks.multiply(BigDecimal.valueOf(100)).divide(totalMarks, 2,
					RoundingMode.HALF_UP);

			result.setPercentage(percentage);

			result.setPassed(obtainedMarks.compareTo(passingMarks) >= 0);

			result.setGrade(calculateGrade(percentage));
		}

		result.setRemarks(request.getRemarks());

		result.setStatus(request.getStatus() != null ? request.getStatus() : ResultStatus.DRAFT);
	}

	public static String calculateGrade(BigDecimal percentage) {

		if (percentage.compareTo(BigDecimal.valueOf(90)) >= 0) {
			return "A+";
		}

		if (percentage.compareTo(BigDecimal.valueOf(80)) >= 0) {
			return "A";
		}

		if (percentage.compareTo(BigDecimal.valueOf(70)) >= 0) {
			return "B+";
		}

		if (percentage.compareTo(BigDecimal.valueOf(60)) >= 0) {
			return "B";
		}

		if (percentage.compareTo(BigDecimal.valueOf(50)) >= 0) {
			return "C";
		}

		if (percentage.compareTo(BigDecimal.valueOf(40)) >= 0) {
			return "D";
		}

		return "F";
	}

	private void validateExamSubjectBelongsToExam(Exam exam, ExamSubject examSubject) {

		if (!examSubject.getExam().getId().equals(exam.getId())) {

			throw new IllegalArgumentException("Exam subject does not belong to the selected exam");
		}
	}

	private void validateStudentBelongsToExamClass(Exam exam, Student student) {

		if (!student.getSchoolClass().getId().equals(exam.getSchoolClass().getId())) {

			throw new IllegalArgumentException("Student does not belong to the exam class");
		}
	}

	private void validateStatusTransition(ResultStatus current, ResultStatus next) {

		if (current == ResultStatus.PUBLISHED && next != ResultStatus.DRAFT) {

			throw new IllegalStateException("Published result can only be reopened as DRAFT");
		}

		if (current == ResultStatus.CANCELLED) {

			throw new IllegalStateException("Cancelled result cannot be changed");
		}
	}

	private Exam getExam(Long id) {

		return examRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Exam not found: " + id));
	}

	private ExamSubject getExamSubject(Long id) {

		return examSubjectRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Exam subject not found: " + id));
	}

	private Student getStudent(Long id) {

		return studentRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Student not found: " + id));
	}

	private ExamResult getResult(Long id) {

		return examResultRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Exam result not found: " + id));
	}

	public List<ExamResultResponse> search(ExamResultFilterRequest request) {
		Specification<ExamResult> specification = byFilters(request.getExamId(), request.getExamSubjectId(),
				request.getStudentId(), request.getClassId(), request.getStatus(), request.getAbsent(),
				request.getPassed());
		return examResultRepository.findAll(specification).stream().map(this::mapToResponse).toList();
	}

	private ExamResultResponse mapToResponse(ExamResult result) {
		return ExamResultResponse.builder().id(result.getId()).examId(result.getExam().getId())
				.examTitle(result.getExam().getTitle()).examSubjectId(result.getExamSubject().getId())
				.subjectId(result.getExamSubject().getSubject().getId()).grade(result.getGrade())
				.subjectName(result.getExamSubject().getSubject().getSubjectName())
				.studentId(result.getStudent().getId()).studentName(result.getStudent().getStudentUser().getFullName())
				.totalMarks(result.getTotalMarks()).passingMarks(result.getPassingMarks())
				.obtainedMarks(result.getObtainedMarks()).percentage(result.getPercentage()).grade(result.getGrade())
				.absent(result.getAbsent()).passed(result.getPassed()).remarks(result.getRemarks())
				.status(result.getStatus()).build();
	}

	public static Specification<ExamResult> byFilters(Long examId, Long examSubjectId, Long studentId, Long classId,
			ResultStatus status, Boolean absent, Boolean passed) {

		return (root, query, cb) -> {

			List<Predicate> predicates = new ArrayList<>();

			/*
			 * Exam filter
			 */
			if (examId != null) {
				predicates.add(cb.equal(root.get("exam").get("id"), examId));
			}

			/*
			 * Exam subject filter
			 */
			if (examSubjectId != null) {
				predicates.add(cb.equal(root.get("examSubject").get("id"), examSubjectId));
			}

			/*
			 * Student filter
			 */
			if (studentId != null) {
				predicates.add(cb.equal(root.get("student").get("id"), studentId));
			}

			/*
			 * Class filter
			 */
			if (classId != null) {
				predicates.add(cb.equal(root.get("exam").get("schoolClass").get("id"), classId));
			}

			/*
			 * Result status
			 */
			if (status != null) {
				predicates.add(cb.equal(root.get("status"), status));
			}

			/*
			 * Absent
			 */
			if (absent != null) {
				predicates.add(cb.equal(root.get("absent"), absent));
			}

			/*
			 * Passed
			 */
			if (passed != null) {
				predicates.add(cb.equal(root.get("passed"), passed));
			}

			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}
}