package com.hipro.school.backend.controllers;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.dtos.StudentConcessionRequest;
import com.hipro.school.backend.dtos.StudentConcessionResponse;
import com.hipro.school.backend.entities.StudentConcession;
import com.hipro.school.backend.services.StudentConcessionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "Student Concession APIs", description = "APIs for managing student fee concessions")
@RestController
@RequestMapping("/v1/student-concessions")
@RequiredArgsConstructor
public class StudentConcessionController {

	private final StudentConcessionService concessionService;

	@Operation(summary = "Create a student concession", description = "Creates a new student concession using the validated StudentConcessionRequest request body.")
	@PostMapping
	public ResponseEntity<StudentConcessionResponse> create(@Valid @RequestBody StudentConcessionRequest request) {
		log.info(">> create student concession: {}", request);
		return ResponseEntity.status(HttpStatus.CREATED).body(concessionService.create(request));
	}

	@Operation(summary = "Get all student concessions", description = "Fetches a paginated list of student concessions. Supports optional studentId and feeStructureId filtering via query params.")
	@GetMapping
	public ResponseEntity<PageResponse<StudentConcession>> getAll(@RequestParam(required = false) Long studentId,
			@RequestParam(required = false) Long feeStructureId,
			@PageableDefault(size = 20, sort = "created", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
		log.info(">> get all student concessions: studentId={}, feeStructureId={}", studentId, feeStructureId);
		return ResponseEntity.ok(concessionService.getAll(studentId, feeStructureId, pageable));
	}

	@Operation(summary = "Get a student concession by id", description = "Fetches a student concession by its unique id path parameter.")
	@GetMapping("/{id}")
	public ResponseEntity<StudentConcession> getById(@PathVariable Long id) {
		log.info(">> get student concession by id={}", id);
		return ResponseEntity.ok(concessionService.getById(id));
	}

	@Operation(summary = "Update a student concession", description = "Updates an existing student concession by id using the validated StudentConcessionRequest request body.")
	@PutMapping("/{id}")
	public ResponseEntity<StudentConcessionResponse> update(@PathVariable Long id,
			@Valid @RequestBody StudentConcessionRequest request) {
		log.info(">> update student concession id={}: {}", id, request);
		return ResponseEntity.ok(concessionService.update(id, request));
	}

	@Operation(summary = "Delete a student concession", description = "Deletes a student concession by its id path parameter.")
	@DeleteMapping("/{id}")
	public ResponseEntity<String> delete(@PathVariable Long id) {
		log.info(">> delete student concession id={}", id);
		concessionService.delete(id);
		return ResponseEntity.ok("Student concession deleted successfully");
	}
}
