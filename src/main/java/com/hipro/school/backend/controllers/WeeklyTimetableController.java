package com.hipro.school.backend.controllers;

import java.time.DayOfWeek;
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

import com.hipro.school.backend.dtos.ApiResponse;
import com.hipro.school.backend.dtos.WeeklyTimetableRequest;
import com.hipro.school.backend.dtos.WeeklyTimetableResponse;
import com.hipro.school.backend.security.AuthDetailsProvider;
import com.hipro.school.backend.services.WeeklyTimetableService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "Weekly Timetable APIs", description = "APIs for managing weekly class timetables")
@RestController
@RequestMapping("/v1/weekly-timetables")
@RequiredArgsConstructor
public class WeeklyTimetableController {

	private final WeeklyTimetableService timetableService;

	@Operation(summary = "Create a timetable entry", description = "Creates a new weekly timetable entry using the validated WeeklyTimetableRequest request body.")
	@PostMapping
	public ResponseEntity<WeeklyTimetableResponse> create(@Valid @RequestBody WeeklyTimetableRequest request) {
		log.info(">> create timetable: {}", request);
		return ResponseEntity.status(HttpStatus.CREATED).body(timetableService.create(request));
	}

	@Operation(summary = "Get all timetable entries", description = "Fetches all timetable entries. Supports optional classId and dayOfWeek filtering via query params.")
	@GetMapping
	public ResponseEntity<List<WeeklyTimetableResponse>> getAll(@RequestParam(required = false) Long classId,
			@RequestParam(required = false) DayOfWeek dayOfWeek) {
		log.info(">> get all timetables: classId={}, dayOfWeek={}", classId, dayOfWeek);
		return ResponseEntity.ok(timetableService.getAll(classId, dayOfWeek));
	}

	@Operation(summary = "Get teacher timetable with filters", description = "Fetches timetable for the logged-in teacher. Supports filtering by classId, subjectId, and dayOfWeek.")
	@GetMapping("/teacher")
	public ResponseEntity<ApiResponse<List<WeeklyTimetableResponse>>> getTeacherTimetable(
			@RequestParam(required = false) Long classId,
			@RequestParam(required = false) Long subjectId,
			@RequestParam(required = false) DayOfWeek dayOfWeek) {
		String email = AuthDetailsProvider.getLoggedEmail();
		log.info(">> get teacher timetable: email={}, classId={}, subjectId={}, dayOfWeek={}", email, classId, subjectId, dayOfWeek);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Teacher timetable fetched successfully.",
				timetableService.getTeacherTimetable(email, classId, subjectId, dayOfWeek)));
	}

	@Operation(summary = "Get a timetable entry by id", description = "Fetches a timetable entry by its unique id path parameter.")
	@GetMapping("/{id}")
	public ResponseEntity<WeeklyTimetableResponse> getById(@PathVariable Long id) {
		log.info(">> get timetable by id={}", id);
		return ResponseEntity.ok(timetableService.getById(id));
	}

	@Operation(summary = "Update a timetable entry", description = "Updates an existing timetable entry by id using the validated WeeklyTimetableRequest request body.")
	@PutMapping("/{id}")
	public ResponseEntity<WeeklyTimetableResponse> update(@PathVariable Long id,
			@Valid @RequestBody WeeklyTimetableRequest request) {
		log.info(">> update timetable id={}: {}", id, request);
		return ResponseEntity.ok(timetableService.update(id, request));
	}

	@Operation(summary = "Delete a timetable entry", description = "Deletes a timetable entry by its id path parameter.")
	@DeleteMapping("/{id}")
	public ResponseEntity<String> delete(@PathVariable Long id) {
		log.info(">> delete timetable id={}", id);
		timetableService.delete(id);
		return ResponseEntity.ok("Weekly timetable deleted successfully");
	}
}
