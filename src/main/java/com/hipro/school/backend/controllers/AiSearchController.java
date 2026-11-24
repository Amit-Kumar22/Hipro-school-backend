package com.hipro.school.backend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hipro.school.backend.dtos.ApiResponse;
import com.hipro.school.backend.dtos.ai.AiSearchRequest;
import com.hipro.school.backend.dtos.ai.AiSearchResponse;
import com.hipro.school.backend.security.AuthDetailsProvider;
import com.hipro.school.backend.services.ai.search.AiSearchService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "AI Search APIs", description = "AI-powered natural language database search for admins")
@RestController
@RequestMapping("/v1/ai")
@RequiredArgsConstructor
public class AiSearchController {

	private final AiSearchService aiSearchService;

	@Operation(summary = "Ask a natural language question about school data", description = "Converts a natural language question into a SQL query, executes it read-only against the database, and returns results with an AI summary. Admin/Superadmin only.")
	@PostMapping("/search")
	public ResponseEntity<ApiResponse<AiSearchResponse>> search(@Valid @RequestBody AiSearchRequest request) {
		log.info(">> ai search: {}", request.getQuestion());
		String email = AuthDetailsProvider.getLoggedEmail();
		AiSearchResponse response = aiSearchService.search(request, email);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "AI search completed", response));
	}
}
