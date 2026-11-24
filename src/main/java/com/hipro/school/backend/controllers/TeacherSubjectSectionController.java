package com.hipro.school.backend.controllers;

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

import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.dtos.TSSRequest;
import com.hipro.school.backend.entities.TeacherSubjectSection;
import com.hipro.school.backend.security.AuthDetailsProvider;
import com.hipro.school.backend.services.TeacherSubjectSectionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "Teacher Subject Section APIs", description = "APIs for managing teacher-subject-section mappings")
@RestController
@RequestMapping("/v1/student-subject-section-mapping")
public class TeacherSubjectSectionController {

	@Autowired
	private TeacherSubjectSectionService service;

	@Operation(summary = "Get all student class section records")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Student class section list fetched") })
	@GetMapping
	public ResponseEntity<PageResponse<TeacherSubjectSection>> getAll(Pageable pageable) {
		log.info(">> getAll StudentClassSection {}", pageable);
		String email = AuthDetailsProvider.getLoggedEmail();
		return ResponseEntity.ok(service.getAll(email, pageable));
	}

	@Operation(summary = "Get all student class section records (admin)", description = "Fetches all student class section records.")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Student class section list fetched") })
	@GetMapping("/admin")
	public ResponseEntity<PageResponse<TeacherSubjectSection>> getAllDetail(Pageable pageable) {
		return ResponseEntity.ok(service.getAllDetail(pageable));
	}

	@Operation(summary = "Get student class section by id")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Student class section found"),
			@ApiResponse(responseCode = "404", description = "Student class section not found") })
	@GetMapping("/{id}")
	public ResponseEntity<TeacherSubjectSection> getById(@PathVariable Long id) {
		log.info(">> getStudentClassSectionById {}", id);
		return ResponseEntity.ok(service.getById(id));
	}

	@Operation(summary = "Create student class section")
	@ApiResponses(value = { @ApiResponse(responseCode = "201", description = "Student class section created"),
			@ApiResponse(responseCode = "400", description = "Invalid request") })
	@PostMapping
	public ResponseEntity<Void> create(@RequestBody TSSRequest request) {
		log.info(">> createStudentClassSection {}", request);
		service.create(request);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@Operation(summary = "Update student class section")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Student class section updated"),
			@ApiResponse(responseCode = "404", description = "Student class section not found") })
	@PutMapping("/{id}")
	public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody TSSRequest request) {
		log.info(">> updateStudentClassSection {}", id);
		service.update(id, request);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@Operation(summary = "Delete student class section")
	@ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Student class section deleted"),
			@ApiResponse(responseCode = "404", description = "Student class section not found") })
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		log.info(">> deleteStudentClassSection {}", id);
		service.delete(id);
		return ResponseEntity.noContent().build();
	}
}