package com.hipro.school.backend.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hipro.school.backend.dtos.PeriodRequest;
import com.hipro.school.backend.dtos.PeriodResponse;
import com.hipro.school.backend.entities.Period;
import com.hipro.school.backend.entities.SchoolClass;
import com.hipro.school.backend.exceptions.ResourceNotFoundException;
import com.hipro.school.backend.exceptions.ServiceException;
import com.hipro.school.backend.repositories.PeriodRepository;
import com.hipro.school.backend.repositories.SchoolClassRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PeriodService {

	private final PeriodRepository periodRepository;
	private final SchoolClassRepository schoolClassRepository;

	@Transactional(readOnly = true)
	public List<PeriodResponse> getAll(Long classId) {
		log.info("Fetching periods for class id: {}", classId);
		List<Period> periods;
		if (classId != null) {
			periods = periodRepository.findAllBySchoolClass_IdOrderByPeriodOrderAsc(classId);
		} else {
			periods = periodRepository.findAll().stream().sorted(java.util.Comparator.comparing(Period::getPeriodOrder))
					.toList();
		}
		return periods.stream().map(this::mapToResponse).toList();
	}

	// Create period
	public PeriodResponse create(PeriodRequest request) {
		log.info("Creating period for class id: {}, period order: {}", request.getClassId(), request.getPeriodOrder());
		SchoolClass schoolClass = findClass(request.getClassId());
		validatePeriodOrder(request.getClassId(), request.getPeriodOrder(), null);
		validateTime(request.getStartTime(), request.getEndTime());
		Period period = mapToEntity(request, schoolClass, null);
		Period saved = periodRepository.save(period);
		log.info("Period created successfully with id: {}", saved.getId());
		return mapToResponse(saved);
	}

	/* Get all periods for a class
	@Transactional(readOnly = true)
	public List<PeriodResponse> getAll(Long classId) {
		log.info("Fetching periods for class id: {}", classId);
		List<Period> periods;
		if (classId != null) {
			periods = periodRepository.findAll().stream()
					.filter(period -> period.getSchoolClass().getId().equals(classId)).toList();
		} else {
			periods = periodRepository.findAll();
		}
		return periods.stream().map(this::mapToResponse).toList();
	}*/

	// Get period by id
	@Transactional(readOnly = true)
	public PeriodResponse getById(Long id) {
		log.info("Fetching period by id: {}", id);
		return mapToResponse(findPeriod(id));
	}

	// Update period
	public PeriodResponse update(Long id, PeriodRequest request) {
		log.info("Updating period id: {}", id);
		Period period = findPeriod(id);
		SchoolClass schoolClass = findClass(request.getClassId());
		validatePeriodOrder(request.getClassId(), request.getPeriodOrder(), id);
		validateTime(request.getStartTime(), request.getEndTime());
		mapToEntity(request, schoolClass, period);
		Period updated = periodRepository.save(period);
		log.info("Period updated successfully with id: {}", updated.getId());
		return mapToResponse(updated);
	}

	// Delete period
	public void delete(Long id) {
		log.info("Deleting period id: {}", id);
		Period period = findPeriod(id);
		periodRepository.delete(period);
		log.info("Period deleted successfully with id: {}", id);
	}

	private Period mapToEntity(PeriodRequest request, SchoolClass schoolClass, Period period) {

		if (period == null) {
			period = new Period();
		}

		period.setName(request.getName());
		period.setSchoolClass(schoolClass);
		period.setPeriodOrder(request.getPeriodOrder());
		period.setStartTime(request.getStartTime());
		period.setEndTime(request.getEndTime());
		period.setBreakPeriod(request.getBreakPeriod());

		return period;
	}

	// Validate duplicate period order within the same class.
	private void validatePeriodOrder(Long classId, Integer periodOrder, Long currentId) {
		boolean exists;
		if (currentId == null) {
			exists = periodRepository.existsBySchoolClass_IdAndPeriodOrder(classId, periodOrder);
		} else {
			exists = periodRepository.existsBySchoolClass_IdAndPeriodOrderAndIdNot(classId, periodOrder, currentId);
		}
		if (exists) {
			throw new ServiceException("Period order " + periodOrder + " already exists for this class");
		}
	}

	// Validate period time.
	private void validateTime(java.time.LocalTime startTime, java.time.LocalTime endTime) {
		if (!startTime.isBefore(endTime)) {
			throw new ServiceException("Start time must be before end time");
		}
	}

	private Period findPeriod(Long id) {
		return periodRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Period not found: " + id));
	}

	private SchoolClass findClass(Long id) {
		return schoolClassRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Class not found: " + id));
	}

	private PeriodResponse mapToResponse(Period period) {
		return PeriodResponse.builder().id(period.getId()).name(period.getName())
				.classId(period.getSchoolClass().getId()).className(period.getSchoolClass().getClassName())
				.periodOrder(period.getPeriodOrder()).startTime(period.getStartTime()).endTime(period.getEndTime())
				.breakPeriod(period.getBreakPeriod()).createdAt(period.getCreated()).build();
	}
}