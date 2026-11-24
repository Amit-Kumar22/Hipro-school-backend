package com.hipro.school.backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hipro.school.backend.dtos.ApiResponse;
import com.hipro.school.backend.dtos.BulkOfficeHolidayRequest;
import com.hipro.school.backend.dtos.OfficeHolidayRequest;
import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.entities.Holiday;
import com.hipro.school.backend.services.HolidayService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "Holiday APIs", description = "APIs for managing school holidays")
@RestController
@RequestMapping("/v1/holiday")
public class HolidayController {

	@Autowired
	private HolidayService service;

	@Operation(summary = "Create holidays", description = "Bulk creates holiday records using the validated list of BulkOfficeHolidayRequest.")
	@PostMapping("/add")
	public ResponseEntity<ApiResponse<String>> create(@Valid @RequestBody List<BulkOfficeHolidayRequest> reqs) {
		log.info(">> create holidays: count={}", reqs.size());
		service.create(reqs);
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.success(HttpStatus.CREATED, "Holiday created successfully.", null));
	}

	@Operation(summary = "Get a holiday by id", description = "Fetches a holiday by its unique id path parameter.")
	@GetMapping("/{id}")
	public ResponseEntity<Holiday> getById(@PathVariable Long id) {
		log.info(">> get holiday by id={}", id);
		return ResponseEntity.ok(service.getById(id));
	}

	@Operation(summary = "Get all holidays", description = "Fetches a paginated list of holidays.")
	@GetMapping
	public ResponseEntity<PageResponse<Holiday>> getAll(Pageable pageable) {
		log.info(">> get all holidays: pageable={}", pageable);
		return ResponseEntity.ok(service.getAll(pageable));
	}

	@Operation(summary = "Update a holiday", description = "Updates an existing holiday by id using the validated OfficeHolidayRequest request body.")
	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse<Void>> update(@PathVariable Long id,
			@Valid @RequestBody OfficeHolidayRequest dto) {
		log.info(">> update holiday id={}: {}", id, dto);
		service.update(id, dto);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@Operation(summary = "Delete a holiday", description = "Deletes a holiday by its id path parameter.")
	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
		log.info(">> delete holiday id={}", id);
		service.delete(id);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.NO_CONTENT, "Holiday deleted successfully.", null));
	}
}
