package com.hipro.school.backend.controllers;

import org.springframework.data.domain.Pageable;
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

import com.hipro.school.backend.dtos.NoticeBoardRequest;
import com.hipro.school.backend.dtos.NoticeBoardResponse;
import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.enums.NoticeAudience;
import com.hipro.school.backend.enums.NoticeStatus;
import com.hipro.school.backend.security.AuthDetailsProvider;
import com.hipro.school.backend.services.NoticeBoardService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "Notice Board APIs", description = "APIs for managing notice board announcements")
@RestController
@RequestMapping("/v1/notice-boards")
@RequiredArgsConstructor
public class NoticeBoardController {

	private final NoticeBoardService noticeBoardService;

	@Operation(summary = "Create a notice", description = "Creates a new notice board entry using the validated NoticeBoardRequest request body for the currently logged-in user.")
	@PostMapping
	public ResponseEntity<NoticeBoardResponse> create(@Valid @RequestBody NoticeBoardRequest request) {
		log.info(">> create notice: {}", request);
		String email = AuthDetailsProvider.getLoggedEmail();
		return ResponseEntity.status(HttpStatus.CREATED).body(noticeBoardService.create(email, request));
	}

	@Operation(summary = "Get all notices", description = "Fetches a paginated list of notices. Supports optional audience, status and userId filtering via query params.")
	@GetMapping
	public ResponseEntity<PageResponse<NoticeBoardResponse>> getAll(
			@RequestParam(required = false) NoticeAudience audience,
			@RequestParam(required = false) NoticeStatus status, @RequestParam(required = false) Long userId,
			@PageableDefault(size = 20, sort = "publishDate", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
		log.info(">> get all notices: audience={}, status={}, userId={}", audience, status, userId);
		return ResponseEntity.ok(noticeBoardService.getAll(audience, status,userId, pageable));
	}

	@Operation(summary = "Get a notice by id", description = "Fetches a notice board entry by its unique id path parameter.")
	@GetMapping("/{id}")
	public ResponseEntity<NoticeBoardResponse> getById(@PathVariable Long id) {
		log.info(">> get notice by id={}", id);
		return ResponseEntity.ok(noticeBoardService.getById(id));
	}

	@Operation(summary = "Update a notice", description = "Updates an existing notice board entry by id using the validated NoticeBoardRequest request body.")
	@PutMapping("/{id}")
	public ResponseEntity<NoticeBoardResponse> update(@PathVariable Long id,
			@Valid @RequestBody NoticeBoardRequest request) {
		log.info(">> update notice id={}: {}", id, request);
		return ResponseEntity.ok(noticeBoardService.update(id, request));
	}

	@Operation(summary = "Delete a notice", description = "Deletes a notice board entry by its id path parameter.")
	@DeleteMapping("/{id}")
	public ResponseEntity<String> delete(@PathVariable Long id) {
		log.info(">> delete notice id={}", id);
		noticeBoardService.delete(id);
		return ResponseEntity.ok("Notice deleted successfully");
	}
}
