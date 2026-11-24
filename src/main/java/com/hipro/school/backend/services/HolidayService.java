package com.hipro.school.backend.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hipro.school.backend.dtos.BulkOfficeHolidayRequest;
import com.hipro.school.backend.dtos.OfficeHolidayRequest;
import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.entities.Holiday;
import com.hipro.school.backend.exceptions.ResourceNotFoundException;
import com.hipro.school.backend.repositories.HolidayRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class HolidayService {

	@Autowired
	private HolidayRepository officeHolidayRepository;

	@Transactional
	public void create(List<BulkOfficeHolidayRequest> requests) {
		log.info("Creating {} office holidays", requests.size());
		List<Holiday> holidays = requests.stream().map(request -> {
			Holiday holiday = new Holiday();
			holiday.setHolidayName(request.getHolidayName());
			holiday.setHolidayDate(request.getHolidayDate());
			holiday.setDescription(request.getDescription());
			holiday.setActive(true);
			return holiday;
		}).toList();
		officeHolidayRepository.saveAll(holidays);
		log.info("Successfully created {} office holidays", holidays.size());
	}

	public boolean isHoliday(LocalDate date, String branchCode) {
		return officeHolidayRepository.findByHolidayDate(date).isPresent();
	}

	public Optional<Holiday> checkHoliday(LocalDate date) {
		return officeHolidayRepository.findByHolidayDate(date);
	}

	public Holiday getById(Long id) {
		log.info("Fetching  by id: {}", id);
		return officeHolidayRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("MasterHoliday details not found with id: " + id));
	}

	public PageResponse<Holiday> getAll(Pageable pageable) {
		Page<Holiday> page = officeHolidayRepository.findAll(pageable);
		return PageResponse.from(page);
	}

	@Transactional
	public void update(Long id, OfficeHolidayRequest req) {
		log.info("Updating package category id: {}", id);
		Holiday hd = getById(id);
		hd.setHolidayName(req.getHolidayName());
		hd.setHolidayDate(req.getHolidayDate());
		hd.setDescription(req.getDescription());
		hd.setActive(true);
	}

	public void delete(Long id) {
		log.info("Deleting package category id: {}", id);
		officeHolidayRepository.deleteById(id);
	}

}
