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

import com.hipro.school.backend.dtos.ExamQuestionRequest;
import com.hipro.school.backend.dtos.ExamQuestionResponse;
import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.services.ExamQuestionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Exam Question APIs", description = "APIs for managing questions mapped to exams")
@RestController
@RequestMapping("/v1/exam-questions")
@RequiredArgsConstructor
@Slf4j
public class ExamQuestionController {

	private final ExamQuestionService examQuestionService;

	@Operation(summary = "Get exam questions", description = "Fetches a paginated list of questions mapped to exams."
			+ " Supports optional examId and questionId filtering via query params.")
	@GetMapping
	public ResponseEntity<PageResponse<ExamQuestionResponse>> getExamQuestions(
			@RequestParam(required = false) Long examId, @RequestParam(required = false) Long questionId,
			Pageable pageable) {
		log.info("Fetching exam questions for school code: {}, exam id: {}, question id: {} with pageable: {}", examId,
				questionId, pageable);
		return ResponseEntity.ok(examQuestionService.getExamQuestions(examId, questionId, pageable));
	}

	@Operation(summary = "Create an exam question", description = "Maps an existing question to an existing exam using the "
			+ "validated ExamQuestionRequest request body. Rejects duplicates when the question is already mapped to the exam.")
	@PostMapping
	public ResponseEntity<ExamQuestionResponse> create(@Valid @RequestBody ExamQuestionRequest request) {
		log.info("Creating new exam question: {}", request);
		return ResponseEntity.status(HttpStatus.CREATED).body(examQuestionService.create(request));
	}

	@Operation(summary = "Get an exam question by id", description = "Fetches a single exam-question mapping by its"
			+ " unique id path parameter, including the full question details.")
	@GetMapping("/{id}")
	public ResponseEntity<ExamQuestionResponse> getById(@PathVariable Long id) {
		log.info("Fetching exam question by id: {}", id);
		return ResponseEntity.ok(examQuestionService.getById(id));
	}

	@Operation(summary = "Update an exam question", description = "Updates an existing exam-question mapping by"
			+ " id using the validated ExamQuestionRequest request body.")
	@PutMapping("/{id}")
	public ResponseEntity<ExamQuestionResponse> update(@PathVariable Long id,
			@Valid @RequestBody ExamQuestionRequest request) {
		log.info("Updating exam question id: {} with request: {}", id, request);
		return ResponseEntity.ok(examQuestionService.update(id, request));
	}

	@Operation(summary = "Delete an exam question", description = "Removes the mapping between an exam "
			+ "and a question by its id path parameter.")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		log.info("Deleting exam question id: {}", id);
		examQuestionService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
