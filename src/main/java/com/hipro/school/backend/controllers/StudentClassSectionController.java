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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hipro.school.backend.dtos.ClassWiseStudentResponse;
import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.dtos.StudentClassRequest;
import com.hipro.school.backend.entities.StudentClassSection;
import com.hipro.school.backend.services.StudentClassSectionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "Student Class Section APIs", description = "APIs for managing student class section records")
@RestController
@RequestMapping("/v1/student-class-sections")
public class StudentClassSectionController {

	@Autowired
	private StudentClassSectionService service;

	@Operation(summary = "Get all student class section records")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Student class section list fetched") })
	// @GetMapping
	public ResponseEntity<PageResponse<StudentClassSection>> getAll(Pageable pageable) {
		log.info(">> getAll StudentClassSection {}", pageable);
		return ResponseEntity.ok(service.getAll(pageable));
	}

	@Operation(summary = "Get class wise students", description = "Fetches students of a class. Pass sectionId to get a specific section's students; omit it to get all sections.")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Class wise students fetched") })
	@GetMapping("/class/{classId}/students")
	public ResponseEntity<PageResponse<ClassWiseStudentResponse>> getStudentsByClass(@PathVariable Long classId,
			@RequestParam(required = false) Long sectionId, Pageable pageable) {
		log.info(">> getStudentsByClass classId={}, sectionId={}", classId, sectionId);
		return ResponseEntity.ok(service.getStudentsByClass(classId, sectionId, pageable));
	}

	@Operation(summary = "Get student class section by id")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Student class section found"),
			@ApiResponse(responseCode = "404", description = "Student class section not found") })
	@GetMapping("/{id}")
	public ResponseEntity<StudentClassSection> getById(@PathVariable Long id) {
		log.info(">> getStudentClassSectionById {}", id);
		return ResponseEntity.ok(service.getById(id));
	}

	@Operation(summary = "Create student class section")
	@ApiResponses(value = { @ApiResponse(responseCode = "201", description = "Student class section created"),
			@ApiResponse(responseCode = "400", description = "Invalid request") })
	@PostMapping
	public ResponseEntity<StudentClassSection> create(@RequestBody StudentClassRequest request) {
		log.info(">> createStudentClassSection {}", request);
		StudentClassSection response = service.create(request);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@Operation(summary = "Update student class section")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Student class section updated"),
			@ApiResponse(responseCode = "404", description = "Student class section not found") })
	@PutMapping("/{id}")
	public ResponseEntity<StudentClassSection> update(@PathVariable Long id, @RequestBody StudentClassRequest request) {
		log.info(">> updateStudentClassSection {}", id);
		return ResponseEntity.ok(service.update(id, request));
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