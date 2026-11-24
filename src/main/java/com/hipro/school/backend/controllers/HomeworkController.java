package com.hipro.school.backend.controllers;

import java.time.DayOfWeek;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
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
import com.hipro.school.backend.dtos.HomeworkRequest;
import com.hipro.school.backend.dtos.HomeworkResponse;
import com.hipro.school.backend.dtos.NoteRequest;
import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.security.AuthDetailsProvider;
import com.hipro.school.backend.services.HomeworkService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "Homework APIs", description = "APIs for managing homework and daily homework notes")
@RestController
@RequestMapping("/v1/home-work")
public class HomeworkController {

	@Autowired
	private HomeworkService service;

	//@Operation(summary = "Get all homework", description = "Fetches a paginated list of all homework records.")
	//@GetMapping
	public ResponseEntity<PageResponse<HomeworkResponse>> getAll(Pageable pageable) {
		return ResponseEntity.ok(service.getAll(pageable));
	}

	@Operation(summary = "Get homework by filters", description = "Fetches homework filtered by teacher, section, subject, and date range.")
	@GetMapping("/teacher")
	public ResponseEntity<PageResponse<HomeworkResponse>> getAllHomeWork(@RequestParam(required = false) Long teacherId,
			@RequestParam(required = false) Long sectionId, @RequestParam(required = false) Long subjectId,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
			Pageable pageable) {
		String email = AuthDetailsProvider.getLoggedEmail();
		return ResponseEntity
				.ok(service.getAllHomeWork(teacherId, sectionId, subjectId, fromDate, toDate, email, pageable));
	}

	@Operation(summary = "Get all homework (admin)", description = "Fetches paginated homework with filters: classId, subjectId, teacherId, dayOfWeek, periodId, homeworkDateFrom/To, dueDateFrom/To, noteStatus, keyword.")
	@GetMapping("/admin")
	public ResponseEntity<ApiResponse<PageResponse<HomeworkResponse>>> getAdminHomework(
			@RequestParam(required = false) Long classId,
			@RequestParam(required = false) Long subjectId,
			@RequestParam(required = false) Long teacherId,
			@RequestParam(required = false) DayOfWeek dayOfWeek,
			@RequestParam(required = false) Long periodId,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate homeworkDateFrom,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate homeworkDateTo,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDateFrom,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDateTo,
			@RequestParam(required = false) String noteStatus,
			@RequestParam(required = false) String keyword,
			Pageable pageable) {
		log.info("Admin homework search: classId={}, subjectId={}, teacherId={}, dayOfWeek={}, periodId={}, homeworkDate={} to {}, dueDate={} to {}, noteStatus={}, keyword={}",
				classId, subjectId, teacherId, dayOfWeek, periodId, homeworkDateFrom, homeworkDateTo, dueDateFrom, dueDateTo, noteStatus, keyword);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Homework fetched successfully.",
				service.getAdminHomework(classId, subjectId, teacherId, dayOfWeek, periodId,
						homeworkDateFrom, homeworkDateTo, dueDateFrom, dueDateTo, noteStatus, keyword, pageable)));
	}

	@Operation(summary = "Get homework by id", description = "Fetches a single homework record by its id.")
	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<HomeworkResponse>> getById(@PathVariable Long id) {
		log.info("Fetching homework by id: {}", id);
		return ResponseEntity
				.ok(ApiResponse.success(HttpStatus.OK, "Homework fetched successfully.", service.getById(id)));
	}

	@Operation(summary = "Create homework", description = "Creates a new homework record with notes.")
	@PostMapping
	public ResponseEntity<ApiResponse<String>> create(@RequestBody HomeworkRequest request) {
		log.info("Creating homework: {}", request);
		service.create(request);
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.success(HttpStatus.CREATED, "Homework created successfully.", null));
	}

	@Operation(summary = "Add daily homework note", description = "Adds a new note to an existing homework record.")
	@PostMapping("/add-daily")
	public ResponseEntity<ApiResponse<String>> addDailyHomework(@RequestParam Long homeworkId,
			@RequestBody NoteRequest request) {
		log.info("Adding daily homework note for homework id: {}", homeworkId);
		service.addDailyHomework(homeworkId, request);
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.success(HttpStatus.CREATED, "Homework note added successfully.", null));
	}

	@Operation(summary = "Delete homework", description = "Deletes a homework record by its id.")
	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
		log.info("Deleting homework id: {}", id);
		service.delete(id);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.NO_CONTENT, "Homework deleted successfully.", null));
	}
}
