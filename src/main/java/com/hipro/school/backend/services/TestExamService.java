package com.hipro.school.backend.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hipro.school.backend.dtos.TestExamResponse;
import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.dtos.TestExamRequest;
import com.hipro.school.backend.entities.ExamSection;
import com.hipro.school.backend.entities.SchoolClass;
import com.hipro.school.backend.entities.Subject;
import com.hipro.school.backend.entities.TestExam;
import com.hipro.school.backend.enums.ExamStatus;
import com.hipro.school.backend.exceptions.ResourceNotFoundException;
import com.hipro.school.backend.repositories.SchoolClassRepository;
import com.hipro.school.backend.repositories.SubjectRepository;
import com.hipro.school.backend.repositories.TestExamRepository;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TestExamService {

	private final TestExamRepository examRepository;

	private final SchoolClassRepository schoolClassRepository;

	private final SubjectRepository subjectRepository;

	@Transactional(readOnly = true)
	public PageResponse<TestExamResponse> getTestExams(Long classId, Long subjectId, Long sectionId, ExamStatus status,
			Pageable pageable) {
		log.info("Fetching exams for class id: {}, subject id: {}, section id: {}", classId, subjectId, sectionId);
		if (classId != null && !schoolClassRepository.existsById(classId)) {
			throw new ResourceNotFoundException("Class not found: " + classId);
		}
		if (subjectId != null && !subjectRepository.existsById(subjectId)) {
			throw new ResourceNotFoundException("Subject not found: " + subjectId);
		}
		Specification<TestExam> specification = byFilters(classId, subjectId, sectionId, status);
		Page<TestExamResponse> page = examRepository.findAll(specification, pageable).map(this::mapToResponse);
		return PageResponse.from(page);
	}

	@Transactional(readOnly = true)
	public TestExamResponse getById(Long id) {
		log.info("Fetching exam by id: {}", id);
		TestExam exam = examRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Exam not found: " + id));

		return mapToResponse(exam);
	}

	public void create(TestExamRequest request) {
		log.info("Creating exam: {}", request.getTitleName());
		SchoolClass schoolClass = schoolClassRepository.findById(request.getSchoolClassId())
				.orElseThrow(() -> new ResourceNotFoundException("Class not found: " + request.getSchoolClassId()));

		Subject subject = subjectRepository.findById(request.getSubjectId())
				.orElseThrow(() -> new ResourceNotFoundException("Subject not found: " + request.getSubjectId()));

		validateMarks(request.getTotalMarks(), request.getPassingMarks());

		TestExam exam = TestExam.builder().titleName(request.getTitleName()).description(request.getDescription())
				.schoolClass(schoolClass).subject(subject).durationMinutes(request.getDurationMinutes())
				.totalMarks(request.getTotalMarks()).passingMarks(request.getPassingMarks())
				.startDate(request.getStartDate()).status(request.getStatus()).build();
		examRepository.save(exam);
	}

	public TestExamResponse update(Long id, TestExamRequest request) {

		log.info("Updating exam id: {}", id);
		TestExam exam = examRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Exam not found: " + id));

		SchoolClass schoolClass = schoolClassRepository.findById(request.getSchoolClassId())
				.orElseThrow(() -> new ResourceNotFoundException("Class not found: " + request.getSchoolClassId()));

		Subject subject = subjectRepository.findById(request.getSubjectId())
				.orElseThrow(() -> new ResourceNotFoundException("Subject not found: " + request.getSubjectId()));

		validateMarks(request.getTotalMarks(), request.getPassingMarks());

		exam.setTitleName(request.getTitleName());
		exam.setDescription(request.getDescription());
		exam.setSchoolClass(schoolClass);
		exam.setSubject(subject);
		exam.setDurationMinutes(request.getDurationMinutes());
		exam.setTotalMarks(request.getTotalMarks());
		exam.setPassingMarks(request.getPassingMarks());
		exam.setStartDate(request.getStartDate());
		exam.setStatus(request.getStatus());

		// Remove old section mappings
		TestExam updatedExam = examRepository.save(exam);
		return mapToResponse(updatedExam);
	}

	public void delete(Long id) {
		log.info("Deleting exam id: {}", id);
		TestExam exam = examRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Exam not found: " + id));
		examRepository.delete(exam);
	}

	private TestExamResponse mapToResponse(TestExam exam) {

		return TestExamResponse.builder().id(exam.getId()).titleName(exam.getTitleName())
				.description(exam.getDescription())
				.schoolClassId(exam.getSchoolClass() != null ? exam.getSchoolClass().getId() : null)
				.schoolClassName(exam.getSchoolClass() != null ? exam.getSchoolClass().getClassName() : null)
				.subjectId(exam.getSubject() != null ? exam.getSubject().getId() : null)
				.subjectName(exam.getSubject() != null ? exam.getSubject().getSubjectName() : null)
				.durationMinutes(exam.getDurationMinutes()).totalMarks(exam.getTotalMarks())
				.passingMarks(exam.getPassingMarks()).startDate(exam.getStartDate()).status(exam.getStatus()).build();
	}

	private void validateMarks(BigDecimal totalMarks, BigDecimal passingMarks) {
		if (passingMarks.compareTo(totalMarks) > 0) {
			throw new IllegalArgumentException("Passing marks cannot be greater than total marks");
		}
	}

	public static Specification<TestExam> byFilters(Long classId, Long subjectId, Long sectionId, ExamStatus status) {

		return (root, query, cb) -> {

			List<Predicate> predicates = new ArrayList<>();

			// Class filter
			if (classId != null) {
				predicates.add(cb.equal(root.get("schoolClass").get("id"), classId));
			}

			// Subject filter
			if (subjectId != null) {
				predicates.add(cb.equal(root.get("subject").get("id"), subjectId));
			}

			// Section filter
			// TestExam -> ExamSection -> Section
			if (sectionId != null) {

				Join<TestExam, ExamSection> examSection = root.join("examSections", JoinType.INNER);

				predicates.add(cb.equal(examSection.get("section").get("id"), sectionId));

				// Avoid duplicate TestExam records
				query.distinct(true);
			}

			// Status filter
			if (status != null) {
				predicates.add(cb.equal(root.get("status"), status));
			}

			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}
}