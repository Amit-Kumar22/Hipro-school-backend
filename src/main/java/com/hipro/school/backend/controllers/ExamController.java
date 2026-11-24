package com.hipro.school.backend.controllers;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

import com.hipro.school.backend.dtos.ExamRequest;
import com.hipro.school.backend.dtos.ExamResponse;
import com.hipro.school.backend.dtos.ExamSubjectRequest;
import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.enums.ExamStatus;
import com.hipro.school.backend.enums.ExamType;
import com.hipro.school.backend.services.ExamService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "Exam APIs", description = "APIs for managing exams and exam subjects")
@RestController
@RequestMapping("/v1/exams")
@RequiredArgsConstructor
public class ExamController {

	private final ExamService examService;

	@Operation(summary = "Create an exam", description = "Creates a new exam using the validated ExamRequest request body.")
	@PostMapping
	public ResponseEntity<String> create(@Valid @RequestBody ExamRequest request) {
		log.info(">> create exam: {}", request);
		examService.create(request);
		return ResponseEntity.status(HttpStatus.CREATED).body("exam created successfully.");
	}

	@Operation(summary = "Get all exams", description = "Fetches a paginated list of exams. Supports optional classId, examType, status and search filtering via query params.")
	@GetMapping
	public ResponseEntity<PageResponse<ExamResponse>> getAll(@RequestParam(required = false) Long classId,
			@RequestParam(required = false) ExamType examType, @RequestParam(required = false) ExamStatus status,
			@RequestParam(required = false) String search,
			@PageableDefault(size = 20, sort = "startDate", direction = Sort.Direction.DESC) Pageable pageable) {
		log.info(">> get all exams: classId={}, examType={}, status={}, search={}", classId, examType, status, search);
		return ResponseEntity.ok(examService.getAll(classId, examType, status, search, pageable));
	}

	@Operation(summary = "Get an exam by id", description = "Fetches an exam by its unique id path parameter.")
	@GetMapping("/{id}")
	public ResponseEntity<ExamResponse> getById(@PathVariable Long id) {
		log.info(">> get exam by id={}", id);
		return ResponseEntity.ok(examService.getById(id));
	}

	@Operation(summary = "Update an exam", description = "Updates an existing exam by id using the validated ExamRequest request body.")
	@PutMapping("/{id}")
	public ResponseEntity<String> update(@PathVariable Long id, @Valid @RequestBody ExamRequest request) {
		log.info(">> update exam id={}: {}", id, request);
		examService.update(id, request);
		return ResponseEntity.ok("");
	}

	@Operation(summary = "Delete an exam", description = "Deletes an exam by its id path parameter.")
	@DeleteMapping("/{id}")
	public ResponseEntity<String> delete(@PathVariable Long id) {
		log.info(">> delete exam id={}", id);
		examService.delete(id);
		return ResponseEntity.ok("Exam deleted successfully");
	}

	@Operation(summary = "Remove subjects from an exam", description = "Deletes subject mappings from an exam by examSubjectId.")
	@DeleteMapping("/{examId}/subjects")
	public ResponseEntity<String> deleteSubjects(@PathVariable Long examSubjectId) {
		log.info(">> delete exam subjects: examSubjectId={}", examSubjectId);
		examService.deleteSubject(examSubjectId);
		return ResponseEntity.ok("Subjects removed from exam successfully.");
	}

	@Operation(summary = "Add subjects to an exam", description = "Adds one or more subjects to an exam using the validated list of ExamSubjectRequest.")
	@PostMapping("/{examId}/subjects")
	public ResponseEntity<String> addSubjects(@PathVariable Long examId,
			@Valid @RequestBody List<ExamSubjectRequest> subjects) {
		log.info(">> add subjects to exam id={}: {}", examId, subjects);
		examService.addSubjects(examId, subjects);
		return ResponseEntity.status(HttpStatus.CREATED).body("Subjects added to exam successfully.");
	}
}
