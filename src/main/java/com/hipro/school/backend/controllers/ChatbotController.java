package com.hipro.school.backend.controllers;

import java.util.List;

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

import com.hipro.school.backend.dtos.ApiResponse;
import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.entities.Chatbot;
import com.hipro.school.backend.services.ChatbotService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/chatbot")
@Slf4j
@Tag(name = "Chatbot APIs", description = "APIs for managing chatbot questions")
public class ChatbotController {

	@Autowired
	private ChatbotService service;

	@GetMapping("/search")
	@Operation(summary = "Search chatbots by keyword", description = "Fetches chatbot questions matching the given keyword.")
	public ResponseEntity<ApiResponse<List<Chatbot>>> getByKeyword(@RequestParam String keyword) {
		log.info("Searching for chatbots with keyword: {}", keyword);
		List<Chatbot> chatbots = service.findByKeyword(keyword);
		return ResponseEntity
				.ok(ApiResponse.success(HttpStatus.OK, "ChatBot questions fetched successfully.", chatbots));
	}

	@PostMapping
	@Operation(summary = "Create a new chatbot question", description = "Adds a new chatbot question with its keyword.")
	public ResponseEntity<ApiResponse<Void>> create(@Valid @RequestBody Chatbot request) {
		log.info("Creating chatbot with keyword: {}", request.getKeyword());
		service.create(request);
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.success(HttpStatus.CREATED, "ChatBot question added successfully.", null));
	}

	@PutMapping("/{id}")
	@Operation(summary = "Update a chatbot question", description = "Updates the chatbot question identified by the given id.")
	public ResponseEntity<ApiResponse<Chatbot>> update(@PathVariable Long id, @Valid @RequestBody Chatbot request) {
		log.info("Updating chatbot id: {}", id);
		service.update(id, request);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "ChatBot question updated successfully.", null));
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get a chatbot question by id", description = "Fetches the chatbot question for the given id.")
	public ResponseEntity<ApiResponse<Chatbot>> getById(@PathVariable Long id) {
		log.info("Fetching chatbot by id: {}", id);
		Chatbot chatbot = service.getEntity(id);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "ChatBot question fetched successfully.", chatbot));
	}

	@GetMapping
	@Operation(summary = "Get all chatbot questions", description = "Returns a paginated list of chatbot questions.")
	public ResponseEntity<PageResponse<Chatbot>> getAll(Pageable pageable) {
		log.info("Fetching all chatbots with pageable: {}", pageable);
		return ResponseEntity.ok(service.getAll(pageable));
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Delete a chatbot question", description = "Deletes the chatbot question identified by the given id.")
	public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
		log.info("Deleting chatbot id: {}", id);
		service.delete(id);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "ChatBot question deleted successfully.", null));
	}

}