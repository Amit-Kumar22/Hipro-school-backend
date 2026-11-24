package com.hipro.school.backend.controllers;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hipro.school.backend.dtos.FeeStructureRequest;
import com.hipro.school.backend.dtos.FeeStructureResponse;
import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.services.FeeStructureService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Fee Structure APIs", description = "APIs for managing fee structures per class and academic year")
@RestController
@RequestMapping("/v1/fee-structures")
@RequiredArgsConstructor
@Slf4j
public class FeeStructureController {

	private final FeeStructureService feeStructureService;

	@Operation(summary = "Get fee structures", description = "Fetches a paginated list of fee structures. Supports optional classId and academicYearId filtering via query params.")
	@GetMapping
	public ResponseEntity<PageResponse<FeeStructureResponse>> getFeeStructures(
			@RequestParam(required = false) Long classId, Pageable pageable) {
		return ResponseEntity.ok(feeStructureService.getFeeStructures(classId, pageable));
	}

	@Operation(summary = "Create a fee structure", description = "Creates a fee head for a class and academic year using the validated FeeStructureRequest request body. Rejects duplicate fee types for the same class and academic year.")
	@PostMapping
	public ResponseEntity<String> create(@Valid @RequestBody FeeStructureRequest request) {
		log.info("Creating new fee structure: {}", request);
		feeStructureService.create(request);
		return ResponseEntity.status(HttpStatus.CREATED).body("Fee structure has been created successfully");
	}

	@Operation(summary = "Get a fee structure by id", description = "Fetches a single fee structure by its unique id path parameter.")
	@GetMapping("/{id}")
	public ResponseEntity<FeeStructureResponse> getById(@PathVariable Long id) {
		log.info("Fetching fee structure by id: {}", id);
		return ResponseEntity.ok(feeStructureService.getById(id));
	}

	@Operation(summary = "Update a fee structure", description = "Updates an existing fee structure by id using the validated FeeStructureRequest request body.")
	@PutMapping("/{id}")
	public ResponseEntity<String> update(@PathVariable Long id, @Valid @RequestBody FeeStructureRequest request) {
		log.info("Updating fee structure id: {} with request: {}", id, request);
		feeStructureService.update(id, request);
		return ResponseEntity.ok("Fee structure updated successfully");
	}

	@Operation(summary = "Delete a fee structure", description = "Deletes a fee structure by its id path parameter. Existing generated student fees are not affected.")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		log.info("Deleting fee structure id: {}", id);
		feeStructureService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
