package com.hipro.school.backend.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.dtos.StudentConcessionRequest;
import com.hipro.school.backend.dtos.StudentConcessionResponse;
import com.hipro.school.backend.entities.FeeStructure;
import com.hipro.school.backend.entities.Student;
import com.hipro.school.backend.entities.StudentConcession;
import com.hipro.school.backend.enums.ConcessionStatus;
import com.hipro.school.backend.enums.DiscountType;
import com.hipro.school.backend.exceptions.ResourceNotFoundException;
import com.hipro.school.backend.exceptions.ServiceException;
import com.hipro.school.backend.repositories.FeeStructureRepository;
import com.hipro.school.backend.repositories.StudentConcessionRepository;
import com.hipro.school.backend.repositories.StudentRepository;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StudentConcessionService {

	private final StudentConcessionRepository concessionRepository;
	private final StudentRepository studentRepository;
	private final FeeStructureRepository feeStructureRepository;

	public StudentConcessionResponse create(StudentConcessionRequest request) {
		log.info("Creating concession for student id: {} and fee structure id: {}", request.getStudentId(),
				request.getFeeStructureId());

		Student student = findStudent(request.getStudentId());
		validateDiscount(request);
		StudentConcession concession = mapToEntity(request, student, null);
		if (request.getFeeStructureId() != null) {
			FeeStructure feeStructure = findFeeStructure(request.getFeeStructureId());
			concession.setFeeStructure(feeStructure);
		}
		StudentConcession saved = concessionRepository.save(concession);
		log.info("Student concession created successfully with id: {}", saved.getId());
		return mapToResponse(saved);
	}

	// Get all concessions with filters and pagination
	@Transactional(readOnly = true)
	public PageResponse<StudentConcession> getAll(Long studentId, Long feeStructureId, Pageable pageable) {
		log.info("Fetching concessions - studentId: {}, feeStructureId: {}, pageable: {}", studentId, feeStructureId,
				pageable);
		Specification<StudentConcession> specification = byFilters(studentId, feeStructureId);
		Page<StudentConcession> page = concessionRepository.findAll(specification, pageable);
		return PageResponse.from(page);
	}

	// Get concession by id
	@Transactional(readOnly = true)
	public StudentConcession getById(Long id) {
		log.info("Fetching student concession by id: {}", id);
		return findConcession(id);
	}

	// Update concession
	public StudentConcessionResponse update(Long id, StudentConcessionRequest request) {
		log.info("Updating student concession id: {}", id);
		StudentConcession concession = findConcession(id);
		Student student = findStudent(request.getStudentId());
		validateDiscount(request);
		mapToEntity(request, student, concession);
		if (request.getFeeStructureId() == null) {
			FeeStructure feeStructure = findFeeStructure(request.getFeeStructureId());
			concession.setFeeStructure(feeStructure);
		}
		StudentConcession updated = concessionRepository.save(concession);
		log.info("Student concession updated successfully with id: {}", updated.getId());
		return mapToResponse(updated);
	}

	// Delete concession
	public void delete(Long id) {
		log.info("Deleting student concession id: {}", id);
		StudentConcession concession = findConcession(id);
		concessionRepository.delete(concession);
		log.info("Student concession deleted successfully with id: {}", id);
	}

	// Map request to entity.
	private StudentConcession mapToEntity(StudentConcessionRequest request, Student student,
			StudentConcession concession) {
		if (concession == null) {
			concession = new StudentConcession();
		}
		concession.setStudent(student);
		concession.setType(request.getType());
		concession.setDiscountType(request.getDiscountType());
		concession.setDiscountValue(request.getDiscountValue());
		concession.setStatus(ConcessionStatus.ACTIVE);
		concession.setRemarks(request.getRemarks());
		return concession;
	}

	// Validate discount configuration.
	private void validateDiscount(StudentConcessionRequest request) {
		if (request.getDiscountType() == DiscountType.PERCENTAGE) {
			if (request.getDiscountValue().compareTo(new java.math.BigDecimal("100")) > 0) {
				throw new ServiceException("Percentage discount cannot be greater than 100");
			}
		}
	}

	private StudentConcession findConcession(Long id) {
		return concessionRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Student concession not found: " + id));
	}

	private Student findStudent(Long id) {
		return studentRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Student not found: " + id));
	}

	private FeeStructure findFeeStructure(Long id) {
		return feeStructureRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Fee structure not found: " + id));
	}

	private StudentConcessionResponse mapToResponse(StudentConcession concession) {
		return StudentConcessionResponse.builder().id(concession.getId()).studentId(concession.getStudent().getId())
				.feeStructureId(concession.getFeeStructure().getId()).type(concession.getType())
				.discountType(concession.getDiscountType()).discountValue(concession.getDiscountValue())
				.status(concession.getStatus()).remarks(concession.getRemarks()).createdAt(concession.getCreated())
				.updated(concession.getUpdated()).build();
	}

	// Specification for filtering concessions.
	private Specification<StudentConcession> byFilters(Long studentId, Long feeStructureId) {
		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			if (studentId != null) {
				predicates.add(cb.equal(root.get("student").get("id"), studentId));
			}
			if (feeStructureId != null) {
				predicates.add(cb.equal(root.get("feeStructure").get("id"), feeStructureId));
			}
			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}
}