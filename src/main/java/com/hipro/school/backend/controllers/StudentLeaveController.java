package com.hipro.school.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.dtos.StudentLeaveRequest;
import com.hipro.school.backend.dtos.StudentLeaveReviewRequest;
import com.hipro.school.backend.entities.StudentLeave;
import com.hipro.school.backend.enums.LeaveStatus;
import com.hipro.school.backend.services.StudentLeaveService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "Student Leave APIs", description = "APIs for managing student leave applications and approvals")
@RestController
@RequestMapping("/v1/student-leaves")
public class StudentLeaveController {

	@Autowired
	private StudentLeaveService service;

	@Operation(summary = "Get all student leaves")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Student leaves fetched") })
	@GetMapping
	public ResponseEntity<PageResponse<StudentLeave>> getAll(@RequestParam String schoolCode,
			@RequestParam(required = false) Long studentId,
			@RequestParam(required = false) LeaveStatus status, Pageable pageable) {
		log.info(">> getAll student leaves ({}, {}, {})", schoolCode, studentId, status);
		return ResponseEntity.ok(service.getAll(schoolCode, studentId, status, pageable));
	}

	@Operation(summary = "Get student leave by id")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Student leave found"),
			@ApiResponse(responseCode = "404", description = "Student leave not found") })
	@GetMapping("/{id}")
	public ResponseEntity<StudentLeave> getById(@PathVariable Long id) {
		log.info(">> getStudentLeaveById {}", id);
		return ResponseEntity.ok(service.getById(id));
	}

	@Operation(summary = "Create student leave")
	@ApiResponses(value = { @ApiResponse(responseCode = "201", description = "Student leave created"),
			@ApiResponse(responseCode = "400", description = "Invalid request"),
			@ApiResponse(responseCode = "409", description = "Overlapping leave exists") })
	@PostMapping
	public ResponseEntity<Void> create(@Valid @RequestBody StudentLeaveRequest request) {
		log.info(">> createStudentLeave {}", request);
		service.create(request);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@Operation(summary = "Update student leave")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Student leave updated"),
			@ApiResponse(responseCode = "400", description = "Invalid request"),
			@ApiResponse(responseCode = "404", description = "Student leave not found"),
			@ApiResponse(responseCode = "409", description = "Leave cannot be updated") })
	@PutMapping("/{id}")
	public ResponseEntity<Void> update(@PathVariable Long id,
			@Valid @RequestBody StudentLeaveRequest request) {
		log.info(">> updateStudentLeave {} {}", id, request);
		service.update(id, request);
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "Approve or reject student leave")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Student leave reviewed"),
			@ApiResponse(responseCode = "404", description = "Student leave not found"),
			@ApiResponse(responseCode = "409", description = "Leave cannot be reviewed") })
	@PatchMapping("/{id}/review")
	public ResponseEntity<Void> review(@PathVariable Long id,
			@Valid @RequestBody StudentLeaveReviewRequest request) {
		log.info(">> reviewStudentLeave {} {}", id, request);
		service.review(id, request);
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "Cancel student leave")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Student leave cancelled"),
			@ApiResponse(responseCode = "404", description = "Student leave not found"),
			@ApiResponse(responseCode = "409", description = "Leave cannot be cancelled") })
	@PatchMapping("/{id}/cancel")
	public ResponseEntity<Void> cancel(@PathVariable Long id) {
		log.info(">> cancelStudentLeave {}", id);
		service.cancel(id);
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "Delete student leave")
	@ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Student leave deleted"),
			@ApiResponse(responseCode = "404", description = "Student leave not found") })
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		log.info(">> deleteStudentLeave {}", id);
		service.delete(id);
		return ResponseEntity.noContent().build();
	}

}
