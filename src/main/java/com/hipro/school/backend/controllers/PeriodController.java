package com.hipro.school.backend.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hipro.school.backend.dtos.PeriodRequest;
import com.hipro.school.backend.dtos.PeriodResponse;
import com.hipro.school.backend.services.PeriodService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "Period APIs", description = "APIs for managing school periods/time slots")
@RestController
@RequestMapping("/v1/periods")
@RequiredArgsConstructor
public class PeriodController {

	private final PeriodService periodService;

	@Operation(summary = "Create a period", description = "Creates a new period using the validated PeriodRequest request body.")
	@PostMapping
	public ResponseEntity<PeriodResponse> create(@Valid @RequestBody PeriodRequest request) {
		log.info(">> create period: {}", request);
		return ResponseEntity.status(HttpStatus.CREATED).body(periodService.create(request));
	}

	@Operation(summary = "Get all periods", description = "Fetches all periods. Supports optional classId filtering via query param.")
	@GetMapping
	public ResponseEntity<List<PeriodResponse>> getAll(@RequestParam(required = false) Long classId) {
		log.info(">> get all periods: classId={}", classId);
		return ResponseEntity.ok(periodService.getAll(classId));
	}

	@Operation(summary = "Get a period by id", description = "Fetches a period by its unique id path parameter.")
	@GetMapping("/{id}")
	public ResponseEntity<PeriodResponse> getById(@PathVariable Long id) {
		log.info(">> get period by id={}", id);
		return ResponseEntity.ok(periodService.getById(id));
	}

	@Operation(summary = "Update a period", description = "Updates an existing period by id using the validated PeriodRequest request body.")
	@PutMapping("/{id}")
	public ResponseEntity<PeriodResponse> update(@PathVariable Long id, @Valid @RequestBody PeriodRequest request) {
		log.info(">> update period id={}: {}", id, request);
		return ResponseEntity.ok(periodService.update(id, request));
	}

	@Operation(summary = "Delete a period", description = "Deletes a period by its id path parameter.")
	@DeleteMapping("/{id}")
	public ResponseEntity<String> delete(@PathVariable Long id) {
		log.info(">> delete period id={}", id);
		periodService.delete(id);
		return ResponseEntity.ok("Period deleted successfully");
	}
}
