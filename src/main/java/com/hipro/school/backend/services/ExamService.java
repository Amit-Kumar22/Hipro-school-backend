package com.hipro.school.backend.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.hipro.school.backend.dtos.ExamRequest;
import com.hipro.school.backend.dtos.ExamResponse;
import com.hipro.school.backend.dtos.ExamSubjectRequest;
import com.hipro.school.backend.dtos.ExamSubjectResponse;
import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.entities.Exam;
import com.hipro.school.backend.entities.ExamSubject;
import com.hipro.school.backend.entities.SchoolClass;
import com.hipro.school.backend.entities.Subject;
import com.hipro.school.backend.enums.ExamStatus;
import com.hipro.school.backend.enums.ExamType;
import com.hipro.school.backend.exceptions.ResourceNotFoundException;
import com.hipro.school.backend.exceptions.ServiceException;
import com.hipro.school.backend.repositories.ClassSubjectRepository;
import com.hipro.school.backend.repositories.ExamRepository;
import com.hipro.school.backend.repositories.ExamSubjectRepository;
import com.hipro.school.backend.repositories.SchoolClassRepository;
import com.hipro.school.backend.repositories.SubjectRepository;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ExamService {

	private final ExamRepository examRepository;

	private final ExamSubjectRepository examSubjectRepository;

	private final SchoolClassRepository schoolClassRepository;

	private final SubjectRepository subjectRepository;

	private final ClassSubjectRepository classSubjectRepository;

	public void create(ExamRequest request) {
		log.info("Creating exam. title: {}, classId: {}", request.getTitle(), request.getClassId());
		validateExamRequest(request);
		SchoolClass schoolClass = findClass(request.getClassId());
		// validateDuplicateSubjects(request.getSubjects());
		Exam exam = Exam.builder().title(request.getTitle().trim()).examType(request.getExamType())
				.description(StringUtils.hasText(request.getDescription()) ? request.getDescription().trim() : null)
				.schoolClass(schoolClass).startDate(request.getStartDate()).endDate(request.getEndDate())
				.status(request.getStatus()).build();
		examRepository.save(exam);
		log.info("Exam created successfully. id: {}");
	}

	@Transactional(readOnly = true)
	public PageResponse<ExamResponse> getAll(Long classId, ExamType examType, ExamStatus status, String search,
			Pageable pageable) {
		log.info("Fetching exams. classId: {}, type: {}, status: {}, search: {}", classId, examType, status, search);
		Specification<Exam> specification = byFilters(classId, examType, status, search);
		Page<Exam> page = examRepository.findAll(specification, pageable);
		return PageResponse.from(page.map(this::mapToResponse));
	}

	@Transactional(readOnly = true)
	public ExamResponse getById(Long id) {
		log.info("Fetching exam by id: {}", id);
		return mapToResponse(findExam(id));
	}

	public void update(Long id, ExamRequest request) {
		log.info("Updating exam id: {}", id);
		validateExamRequest(request);
		Exam exam = findExam(id);

		SchoolClass schoolClass = findClass(request.getClassId());
		exam.setTitle(request.getTitle().trim());
		exam.setExamType(request.getExamType());
		exam.setDescription(StringUtils.hasText(request.getDescription()) ? request.getDescription().trim() : null);
		exam.setSchoolClass(schoolClass);
		exam.setStartDate(request.getStartDate());
		exam.setEndDate(request.getEndDate());
		validateStatusTransition(exam.getStatus(), request.getStatus());
		exam.setStatus(request.getStatus());
		examRepository.save(exam);
		log.info("Exam updated successfully. id: {}");
	}

	public void delete(Long id) {
		log.info("Deleting exam id: {}", id);
		Exam exam = findExam(id);
		if (exam.getStatus() == ExamStatus.PUBLISHED || exam.getStatus() == ExamStatus.COMPLETED) {
			throw new ServiceException("Ongoing or completed exam cannot be deleted");
		}
		examRepository.delete(exam);
		log.info("Exam deleted successfully. id: {}", id);
	}

	private void validateExamRequest(ExamRequest request) {
		if (request.getStartDate().isAfter(request.getEndDate())) {
			throw new ServiceException("Exam end date must be after start date");
		}
	}

	private void validateMarks(ExamSubjectRequest request) {
		if (request.getPassingMarks().compareTo(request.getTotalMarks()) > 0) {
			throw new ServiceException("Passing marks cannot be greater than total marks");
		}
	}

	private void validateExamSubjectDate(LocalDateTime examDate, LocalDateTime startDate, LocalDateTime endDate) {
		if (examDate.isBefore(startDate) || examDate.isAfter(endDate)) {
			throw new ServiceException("Subject exam date must be within exam start and end date");
		}
	}

	private void validateDuplicateSubjects(List<ExamSubjectRequest> subjects) {
		Set<Long> subjectIds = new HashSet<>();
		for (ExamSubjectRequest subject : subjects) {
			if (!subjectIds.add(subject.getSubjectId())) {
				throw new ServiceException("Duplicate subject found in exam: " + subject.getSubjectId());
			}
		}
	}

	private void validateSubjectAssignedToClass(Long classId, Long subjectId) {
		boolean exists = classSubjectRepository.existsBySchoolClass_IdAndSubject_Id(classId, subjectId);
		if (!exists) {
			throw new ServiceException("Subject " + subjectId + " is not assigned to class " + classId);
		}
	}

	private void validateStatusTransition(ExamStatus current, ExamStatus requested) {
		if (current == null || requested == null || current == requested) {
			return;
		}

		if (current == ExamStatus.COMPLETED) {
			throw new ServiceException("Completed exam status cannot be changed");
		}

		if (current == ExamStatus.CANCELLED) {
			throw new ServiceException("Cancelled exam status cannot be changed");
		}
	}

	public static Specification<Exam> byFilters(Long classId, ExamType examType, ExamStatus status, String search) {
		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			if (classId != null) {
				predicates.add(cb.equal(root.get("schoolClass").get("id"), classId));
			}
			if (examType != null) {
				predicates.add(cb.equal(root.get("examType"), examType));
			}
			if (status != null) {
				predicates.add(cb.equal(root.get("status"), status));
			}
			if (StringUtils.hasText(search)) {
				String value = "%" + search.trim().toLowerCase() + "%";
				predicates.add(cb.like(cb.lower(root.get("title")), value));
			}
			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}

	private Exam findExam(Long id) {
		return examRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Exam not found: " + id));
	}

	private SchoolClass findClass(Long id) {
		return schoolClassRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Class not found: " + id));
	}

	private Subject findSubject(Long id) {
		return subjectRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Subject not found: " + id));
	}

	private ExamResponse mapToResponse(Exam exam) {
		List<ExamSubjectResponse> subjects = exam.getExamSubjects().stream().map(this::mapSubjectToResponse).toList();
		return ExamResponse.builder().id(exam.getId()).title(exam.getTitle()).examType(exam.getExamType())
				.description(exam.getDescription()).classId(exam.getSchoolClass().getId())
				.className(exam.getSchoolClass().getClassName()).startDate(exam.getStartDate())
				.endDate(exam.getEndDate()).status(exam.getStatus()).subjects(subjects).createdAt(exam.getCreated())
				.build();
	}

	private ExamSubjectResponse mapSubjectToResponse(ExamSubject examSubject) {
		return ExamSubjectResponse.builder().id(examSubject.getId()).subjectId(examSubject.getSubject().getId())
				.subjectName(examSubject.getSubject().getSubjectName()).examDate(examSubject.getExamDate())
				.durationMinutes(examSubject.getDurationMinutes()).totalMarks(examSubject.getTotalMarks())
				.passingMarks(examSubject.getPassingMarks()).build();
	}

	public void addSubjects(Long examId, List<ExamSubjectRequest> subjects) {
		log.info("Adding {} subjects to exam: {}", subjects.size(), examId);
		Exam exam = findExam(examId);
		if (exam.getStatus() == ExamStatus.COMPLETED || exam.getStatus() == ExamStatus.CANCELLED) {
			throw new ServiceException("Subjects cannot be added to a completed or cancelled exam");
		}
		validateDuplicateSubjects(subjects);
		for (ExamSubjectRequest request : subjects) {
			if (examSubjectRepository.existsByExam_IdAndSubject_Id(examId, request.getSubjectId())) {
				throw new ServiceException(
						"Subject with id " + request.getSubjectId() + " is already added to this exam");
			}
			Subject subject = findSubject(request.getSubjectId());
			validateSubjectAssignedToClass(exam.getSchoolClass().getId(), subject.getId());
			validateMarks(request);
			validateExamSubjectDate(request.getExamDate(), exam.getStartDate(), exam.getEndDate());
			ExamSubject examSubject = ExamSubject.builder().exam(exam).subject(subject).examDate(request.getExamDate())
					.durationMinutes(request.getDurationMinutes()).totalMarks(request.getTotalMarks())
					.passingMarks(request.getPassingMarks()).build();
			examSubjectRepository.save(examSubject);
		}
		log.info("Successfully added subjects to exam: {}", examId);
	}

	public void deleteSubject(Long examSubjectId) {
		log.info("Deleting exam subject: {}", examSubjectId);
		ExamSubject examSubject = examSubjectRepository.findById(examSubjectId)
				.orElseThrow(() -> new ResourceNotFoundException("Exam subject not found: " + examSubjectId));
		Exam exam = examSubject.getExam();
		if (exam.getStatus() == ExamStatus.COMPLETED || exam.getStatus() == ExamStatus.CANCELLED) {
			throw new ServiceException("Subject cannot be removed from a completed or cancelled exam");
		}
		examSubjectRepository.delete(examSubject);
		log.info("Exam subject deleted successfully: {}", examSubjectId);
	}
}