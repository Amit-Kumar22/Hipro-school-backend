package com.hipro.school.backend.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hipro.school.backend.dtos.FeeStructureRequest;
import com.hipro.school.backend.dtos.FeeStructureResponse;
import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.entities.FeeStructure;
import com.hipro.school.backend.entities.SchoolClass;
import com.hipro.school.backend.exceptions.ResourceNotFoundException;
import com.hipro.school.backend.exceptions.ServiceException;
import com.hipro.school.backend.repositories.FeeStructureRepository;
import com.hipro.school.backend.repositories.SchoolClassRepository;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FeeStructureService {

	private final FeeStructureRepository feeStructureRepository;
	private final SchoolClassRepository schoolClassRepository;

	public void create(FeeStructureRequest request) {
		log.info("Creating fee structure for class id: {} and academic year id: {}", request.getClassId(),
				request.getAcademicYear());
		SchoolClass schoolClass = findClass(request.getClassId());
		if (feeStructureRepository.existsByClassRoom_IdAndFeeType(schoolClass.getId(), request.getFeeType())) {
			throw new ServiceException(
					"Fee type " + request.getFeeType() + " already configured for this class and academic year");
		}
		FeeStructure feeStructure = mapToEntity(request, schoolClass, null);
		feeStructureRepository.save(feeStructure);
	}

	@Transactional(readOnly = true)
	public PageResponse<FeeStructureResponse> getFeeStructures(Long classId, Pageable pageable) {
		log.info("Fetching fee structures for class id: {}, academic year id: {} with pageable: {}", classId, pageable);
		Page<FeeStructure> page = feeStructureRepository.findAll(byFilters(classId), pageable);
		return PageResponse.from(page.map(this::mapToResponse));
	}

	@Transactional(readOnly = true)
	public FeeStructureResponse getById(Long id) {
		log.info("Fetching fee structure by id: {}", id);
		return mapToResponse(findFeeStructure(id));
	}

	public void update(Long id, FeeStructureRequest request) {
		log.info("Updating fee structure id: {} with request: {}", id, request);
		FeeStructure feeStructure = findFeeStructure(id);
		SchoolClass schoolClass = findClass(request.getClassId());

		if (!feeStructure.getClassRoom().getId().equals(schoolClass.getId())
				|| !feeStructure.getFeeType().equals(request.getFeeType())) {
			if (feeStructureRepository.existsByClassRoom_IdAndFeeType(schoolClass.getId(), request.getFeeType())) {
				throw new ServiceException(
						"Fee type " + request.getFeeType() + " already configured for this class and academic year");
			}
		}
		mapToEntity(request, schoolClass, feeStructure);
		feeStructureRepository.save(feeStructure);
	}

	public void delete(Long id) {
		log.info("Deleting fee structure id: {}", id);
		feeStructureRepository.delete(findFeeStructure(id));
		log.info("Deleted fee structure id: {}", id);
	}

	private FeeStructure findFeeStructure(Long id) {
		return feeStructureRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Fee structure not found: " + id));
	}

	private SchoolClass findClass(Long classId) {
		return schoolClassRepository.findById(classId)
				.orElseThrow(() -> new ResourceNotFoundException("Class not found: " + classId));
	}

	private FeeStructureResponse mapToResponse(FeeStructure feeStructure) {
		return FeeStructureResponse.builder().id(feeStructure.getId()).classId(feeStructure.getClassRoom().getId())
				.title(feeStructure.getTitle()).className(feeStructure.getClassRoom().getClassName())
				.feeType(feeStructure.getFeeType()).academicYear(feeStructure.getAcademicYear())
				.amount(feeStructure.getAmount()).frequency(feeStructure.getFrequency())
				.createdAt(feeStructure.getCreated()).build();
	}

	private FeeStructure mapToEntity(FeeStructureRequest request, SchoolClass schoolClass, FeeStructure feeStructure) {
		if (feeStructure == null) {
			feeStructure = new FeeStructure();
		}
		feeStructure.setTitle(request.getTitle());
		feeStructure.setClassRoom(schoolClass);
		feeStructure.setFeeType(request.getFeeType());
		feeStructure.setAmount(request.getAmount());
		feeStructure.setAcademicYear(request.getAcademicYear());
		feeStructure.setDescription(request.getDescription());
		feeStructure.setFrequency(request.getFrequency());
		return feeStructure;
	}

	public static Specification<FeeStructure> byFilters(Long classId) {
		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			// Optional class filter.
			if (classId != null) {
				predicates.add(cb.equal(root.get("classRoom").get("id"), classId));
			}
			// Optional academic year filter.
			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}
}
