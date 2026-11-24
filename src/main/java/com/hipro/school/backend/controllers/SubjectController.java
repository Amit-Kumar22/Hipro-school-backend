package com.hipro.school.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hipro.school.backend.dtos.ApiResponse;
import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.dtos.SubjectRequest;
import com.hipro.school.backend.entities.Subject;
import com.hipro.school.backend.services.SubjectService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Subject APIs", description = "APIs for managing subjects")
@RestController
@RequestMapping("/v1/subject")
@Slf4j
public class SubjectController {

	@Autowired
	private SubjectService subjectService;

	@Operation(summary = "Get all subjects", description = "Fetches a paginated list of subjects. Supports optional subject name filtering via the subjectName query param.")
	@GetMapping("/list")
	public ResponseEntity<PageResponse<Subject>> getAllSubject(Pageable pageable,
			@RequestParam(required = false) String subjectName) {
		log.info("Fetching all Subject({}, {})", pageable, subjectName);
		return ResponseEntity.ok(subjectService.getAllSubject(pageable, subjectName));
	}

	@Operation(summary = "Get a subject by id", description = "Fetches a subject by its unique id path parameter.")
	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<Subject>> getSubjectById(@PathVariable Long id) {
		log.info("Fetching get Subject By Id: {}", id);
		return ResponseEntity.ok(
				ApiResponse.success(HttpStatus.OK, "Subject fetched successfully", subjectService.getSubjectById(id)));
	}

	@Operation(summary = "Create a subject", description = "Creates a new subject using the SubjectRequest request body.")
	@PostMapping("/save")
	public ResponseEntity<ApiResponse<String>> saveSubject(@RequestBody SubjectRequest request) {
		log.info("Creating new school class: {}", request);
		subjectService.saveSubject(request);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.CREATED, "Subject saved successfully", null));
	}

	@Operation(summary = "Update a subject", description = "Updates an existing subject by id using the SubjectRequest request body.")
	@PostMapping("/update/{id}")
	public ResponseEntity<ApiResponse<String>> updateSubject(@PathVariable Long id,
			@RequestBody SubjectRequest request) {
		log.info("Updating school class id: {}", id);
		subjectService.updateSubject(id, request);
		return ResponseEntity
				.ok(ApiResponse.success(HttpStatus.NO_CONTENT, "Subject detail updated successfully", null));
	}

	@Operation(summary = "Delete a subject", description = "Deletes a subject by its id path parameter.")
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<ApiResponse<String>> deleteSubject(@PathVariable Long id) {
		log.info("Deleting school class id: {}", id);
		subjectService.deleteSubject(id);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.NO_CONTENT, "Subject deleted successfully", null));
	}

}