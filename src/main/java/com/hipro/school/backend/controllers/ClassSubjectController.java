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

import com.hipro.school.backend.dtos.BulkClassSubjectRequest;
import com.hipro.school.backend.dtos.ClassSubjectRequest;
import com.hipro.school.backend.dtos.ClassSubjectResponse;
import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.services.ClassSubjectService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "Class Subject APIs", description = "APIs for managing subject mappings to classes")
@RestController
@RequestMapping("/v1/class-subjects")
@RequiredArgsConstructor
public class ClassSubjectController {

	private final ClassSubjectService classSubjectService;

	@Operation(summary = "Create a class subject mapping", description = "Creates a new mapping between a class and a subject using the validated ClassSubjectRequest request body.")
	@PostMapping
	public ResponseEntity<ClassSubjectResponse> create(@Valid @RequestBody ClassSubjectRequest request) {
		log.info(">> create class subject mapping: {}", request);
		return ResponseEntity.status(HttpStatus.CREATED).body(classSubjectService.create(request));
	}

	@Operation(summary = "Bulk update class subjects", description = "Bulk creates, updates or deletes subject mappings for a class using the validated BulkClassSubjectRequest request body.")
	@PostMapping("/bulk-update")
	public ResponseEntity<String> bulkUpdate(@Valid @RequestBody BulkClassSubjectRequest request) {
		log.info(">> bulk update class subjects: classId={}", request.getClassId());
		classSubjectService.bulkUpdate(request);
		return ResponseEntity.ok("Subjects updated successfully for the class");
	}

	@Operation(summary = "Get all class subject mappings", description = "Fetches a paginated list of class subject mappings. Supports optional classId and subjectId filtering via query params.")
	@GetMapping
	public ResponseEntity<PageResponse<ClassSubjectResponse>> getAll(@RequestParam(required = false) Long classId,
			@RequestParam(required = false) Long subjectId, Pageable pageable) {
		log.info(">> get all class subjects: classId={}, subjectId={}", classId, subjectId);
		return ResponseEntity.ok(classSubjectService.getAll(classId, subjectId, pageable));
	}

	@Operation(summary = "Get a class subject mapping by id", description = "Fetches a class subject mapping by its unique id path parameter.")
	@GetMapping("/{id}")
	public ResponseEntity<ClassSubjectResponse> getById(@PathVariable Long id) {
		log.info(">> get class subject by id={}", id);
		return ResponseEntity.ok(classSubjectService.getById(id));
	}

	@Operation(summary = "Update a class subject mapping", description = "Updates an existing class subject mapping by id using the validated ClassSubjectRequest request body.")
	@PutMapping("/{id}")
	public ResponseEntity<ClassSubjectResponse> update(@PathVariable Long id,
			@Valid @RequestBody ClassSubjectRequest request) {
		log.info(">> update class subject id={}: {}", id, request);
		return ResponseEntity.ok(classSubjectService.update(id, request));
	}

	@Operation(summary = "Delete a class subject mapping", description = "Deletes a class subject mapping by its id path parameter.")
	@DeleteMapping("/{id}")
	public ResponseEntity<String> delete(@PathVariable Long id) {
		log.info(">> delete class subject id={}", id);
		classSubjectService.delete(id);
		return ResponseEntity.ok("Subject removed from class successfully");
	}
}
