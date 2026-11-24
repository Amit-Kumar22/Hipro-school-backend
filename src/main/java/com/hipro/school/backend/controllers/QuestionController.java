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
import org.springframework.web.bind.annotation.RestController;

import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.dtos.QuestionRequest;
import com.hipro.school.backend.dtos.QuestionResponse;
import com.hipro.school.backend.enums.QuestionType;
import com.hipro.school.backend.services.QuestionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Question APIs", description = "APIs for managing questions")
@RestController
@RequestMapping("/v1/questions")
@RequiredArgsConstructor
@Slf4j
public class QuestionController {

	private final QuestionService questionService;

	@Operation(summary = "Create a question", description = "Creates a new question using the validated QuestionRequest request body.")
	@PostMapping
	public ResponseEntity<QuestionResponse> create(@Valid @RequestBody QuestionRequest request) {
		log.info("Creating new question: {}", request);
		return ResponseEntity.status(HttpStatus.CREATED).body(questionService.create(request));
	}

	@Operation(summary = "Get a question by id", description = "Fetches a question by its unique id path parameter.")
	@GetMapping("/{id}")
	public ResponseEntity<QuestionResponse> getById(@PathVariable Long id) {
		log.info("Fetching question by id: {}", id);
		return ResponseEntity.ok(questionService.getById(id));
	}

	@Operation(summary = "Get all questions", description = "Fetches a paginated list of questions.")
	@GetMapping
	public ResponseEntity<PageResponse<QuestionResponse>> getAll(Pageable pageable) {
				return ResponseEntity.ok(questionService.getAll(pageable));
	}

	@Operation(summary = "Get questions by subject", description = "Fetches a paginated list of questions filtered by the subjectId path parameter.")
	@GetMapping("/subject/{subjectId}")
	public ResponseEntity<PageResponse<QuestionResponse>> getBySubject(@PathVariable Long subjectId,
			Pageable pageable) {
		log.info("Fetching questions by subject id: {} with pageable: {}", subjectId, pageable);
		return ResponseEntity.ok(questionService.getBySubject(subjectId, pageable));
	}

	@Operation(summary = "Get questions by subject and type", description = "Fetches a paginated list of questions filtered by the subjectId and question type path parameters.")
	@GetMapping("/subject/{subjectId}/type/{type}")
	public ResponseEntity<PageResponse<QuestionResponse>> getBySubjectAndType(@PathVariable Long subjectId,
			@PathVariable QuestionType type, Pageable pageable) {
		log.info("Fetching questions by subject id: {} and type: {} with pageable: {}", subjectId, type, pageable);
		return ResponseEntity.ok(questionService.getBySubjectAndType(subjectId, type, pageable));
	}

	@Operation(summary = "Update a question", description = "Updates an existing question by id using the validated QuestionRequest request body.")
	@PutMapping("/{id}")
	public ResponseEntity<QuestionResponse> update(@PathVariable Long id, @Valid @RequestBody QuestionRequest request) {
		log.info("Updating question id: {} with request: {}", id, request);
		return ResponseEntity.ok(questionService.update(id, request));
	}

	@Operation(summary = "Delete a question", description = "Deletes a question by its id path parameter.")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		log.info("Deleting question id: {}", id);
		questionService.delete(id);
		return ResponseEntity.noContent().build();
	}
}