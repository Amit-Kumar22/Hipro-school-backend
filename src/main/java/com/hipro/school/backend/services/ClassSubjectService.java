package com.hipro.school.backend.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hipro.school.backend.dtos.BulkClassSubjectRequest;
import com.hipro.school.backend.dtos.ClassSubjectRequest;
import com.hipro.school.backend.dtos.ClassSubjectResponse;
import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.entities.ClassSubject;
import com.hipro.school.backend.entities.SchoolClass;
import com.hipro.school.backend.entities.Subject;
import com.hipro.school.backend.exceptions.ResourceNotFoundException;
import com.hipro.school.backend.exceptions.ServiceException;
import com.hipro.school.backend.repositories.ClassSubjectRepository;
import com.hipro.school.backend.repositories.SchoolClassRepository;
import com.hipro.school.backend.repositories.SubjectRepository;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ClassSubjectService {

	private final ClassSubjectRepository classSubjectRepository;
	private final SchoolClassRepository schoolClassRepository;
	private final SubjectRepository subjectRepository;

	public ClassSubjectResponse create(ClassSubjectRequest request) {
		log.info("Creating subject mapping. classId: {}, subjectId: {}", request.getClassId(), request.getSubjectId());
		SchoolClass schoolClass = findClass(request.getClassId());
		Subject subject = findSubject(request.getSubjectId());
		validateDuplicate(request.getClassId(), request.getSubjectId(), null);
		ClassSubject classSubject = mapToEntity(request, schoolClass, subject, null);
		ClassSubject saved = classSubjectRepository.save(classSubject);
		log.info("Class subject created successfully. id: {}", saved.getId());
		return mapToResponse(saved);
	}

	@Transactional
	public void bulkUpdate(BulkClassSubjectRequest request) {
		log.info("Bulk updating subjects for class id: {}, subject count: {}", request.getClassId(),
				request.getSubjectIds().size());
		SchoolClass schoolClass = findClass(request.getClassId());
		List<Long> subjectIds = request.getSubjectIds().stream().distinct().toList();
		classSubjectRepository.deleteAllByClassId(schoolClass.getId());

		List<ClassSubject> classSubjects = subjectIds.stream().map(subjectId -> {
			Subject subject = findSubject(subjectId);
			return ClassSubject.builder().schoolClass(schoolClass).subject(subject).build();
		}).toList();
		List<ClassSubject> saved = classSubjectRepository.saveAll(classSubjects);
		log.info("Bulk subject update completed for class id: {}. Total subjects: {}", schoolClass.getId(),
				saved.size());
	}

	@Transactional(readOnly = true)
	public PageResponse<ClassSubjectResponse> getAll(Long classId, Long subjectId, Pageable pageable) {
		log.info("Fetching class subjects. classId: {}, subjectId: {}", classId, subjectId);
		Specification<ClassSubject> specification = byFilters(classId, subjectId);
		Page<ClassSubject> page = classSubjectRepository.findAll(specification, pageable);
		return PageResponse.from(page.map(this::mapToResponse));
	}

	@Transactional(readOnly = true)
	public ClassSubjectResponse getById(Long id) {

		log.info("Fetching class subject by id: {}", id);

		return mapToResponse(findClassSubject(id));
	}

	public ClassSubjectResponse update(Long id, ClassSubjectRequest request) {
		log.info("Updating class subject id: {}", id);
		ClassSubject classSubject = findClassSubject(id);
		SchoolClass schoolClass = findClass(request.getClassId());
		Subject subject = findSubject(request.getSubjectId());
		validateDuplicate(request.getClassId(), request.getSubjectId(), id);
		mapToEntity(request, schoolClass, subject, classSubject);
		ClassSubject updated = classSubjectRepository.save(classSubject);
		log.info("Class subject updated successfully. id: {}", updated.getId());
		return mapToResponse(updated);
	}

	public void delete(Long id) {
		log.info("Deleting class subject id: {}", id);
		ClassSubject classSubject = findClassSubject(id);
		classSubjectRepository.delete(classSubject);
		log.info("Class subject deleted successfully. id: {}", id);
	}

	private ClassSubject mapToEntity(ClassSubjectRequest request, SchoolClass schoolClass, Subject subject,
			ClassSubject classSubject) {
		if (classSubject == null) {
			classSubject = new ClassSubject();
		}

		classSubject.setSchoolClass(schoolClass);
		classSubject.setSubject(subject);

		return classSubject;
	}

	private void validateDuplicate(Long classId, Long subjectId, Long currentId) {
		boolean exists;
		if (currentId == null) {
			exists = classSubjectRepository.existsBySchoolClass_IdAndSubject_Id(classId, subjectId);
		} else {
			exists = classSubjectRepository.existsBySchoolClass_IdAndSubject_IdAndIdNot(classId, subjectId, currentId);
		}
		if (exists) {
			throw new ServiceException("Subject is already assigned to this class");
		}
	}

	private ClassSubject findClassSubject(Long id) {
		return classSubjectRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Class subject mapping not found: " + id));
	}

	private SchoolClass findClass(Long id) {
		return schoolClassRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Class not found: " + id));
	}

	private Subject findSubject(Long id) {
		return subjectRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Subject not found: " + id));
	}

	private ClassSubjectResponse mapToResponse(ClassSubject classSubject) {
		return ClassSubjectResponse.builder().id(classSubject.getId()).classId(classSubject.getSchoolClass().getId())
				.className(classSubject.getSchoolClass().getClassName()).subjectId(classSubject.getSubject().getId())
				.subjectName(classSubject.getSubject().getSubjectName()).build();
	}

	public static Specification<ClassSubject> byFilters(Long classId, Long subjectId) {
		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			if (classId != null) {
				predicates.add(cb.equal(root.get("schoolClass").get("id"), classId));
			}
			if (subjectId != null) {
				predicates.add(cb.equal(root.get("subject").get("id"), subjectId));
			}
			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}
}