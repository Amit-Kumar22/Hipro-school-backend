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

import com.hipro.school.backend.dtos.TestExamRequest;
import com.hipro.school.backend.dtos.TestExamResponse;
import com.hipro.school.backend.enums.ExamStatus;
import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.services.TestExamService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Test Exam APIs", description = "APIs for managing exams")
@RestController
@RequestMapping("/v1/test-exam")
@RequiredArgsConstructor
@Slf4j
public class TestExamController {

	private final TestExamService examService;

	@Operation(summary = "Get exams", description = "Fetches a paginated list of exams. Supports optional classId, subjectId and sectionId filtering via query params.")
	@GetMapping
	public ResponseEntity<PageResponse<TestExamResponse>> getTestExams(@RequestParam(required = false) Long classId,
			@RequestParam(required = false) Long subjectId, @RequestParam(required = false) Long sectionId,
			@RequestParam(required = false) ExamStatus status, Pageable pageable) {
		log.info("Fetching exams for school code: {}, class id: {}, subject id: {}, section id: {} with pageable: {}",
				classId, subjectId, sectionId, pageable);
		return ResponseEntity.ok(examService.getTestExams(classId, subjectId, sectionId,status, pageable));
	}

	@Operation(summary = "Create an exam", description = "Creates a new exam using the validated ExamRequest request body.")
	@PostMapping
	public ResponseEntity<String> create(@Valid @RequestBody TestExamRequest request) {
		log.info("Creating new exam: {}", request);
		examService.create(request);
		return ResponseEntity.status(HttpStatus.CREATED).body("Test exam created successfully");
	}

	@Operation(summary = "Get an exam by id", description = "Fetches an exam by its unique id path parameter.")
	@GetMapping("/{id}")
	public ResponseEntity<TestExamResponse> getById(@PathVariable Long id) {
		log.info("Fetching exam by id: {}", id);
		return ResponseEntity.ok(examService.getById(id));
	}

	@Operation(summary = "Update an exam", description = "Updates an existing exam by id using the validated ExamRequest request body.")
	@PutMapping("/{id}")
	public ResponseEntity<TestExamResponse> update(@PathVariable Long id, @Valid @RequestBody TestExamRequest request) {
		log.info("Updating exam id: {} with request: {}", id, request);
		return ResponseEntity.ok(examService.update(id, request));
	}

	@Operation(summary = "Delete an exam", description = "Deletes an exam by its id path parameter.")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		log.info("Deleting exam id: {}", id);
		examService.delete(id);
		return ResponseEntity.noContent().build();
	}
}