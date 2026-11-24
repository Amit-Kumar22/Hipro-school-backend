package com.hipro.school.backend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hipro.school.backend.dtos.ApiResponse;
import com.hipro.school.backend.dtos.ai.AiQueryRequest;
import com.hipro.school.backend.dtos.ai.AiQueryResponse;
import com.hipro.school.backend.security.AuthDetailsProvider;
import com.hipro.school.backend.services.ai.AiQueryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "AI Query APIs", description = "AI powered natural language search and reports for admins")
@RestController
@RequestMapping("/v1/ai")
@RequiredArgsConstructor
public class AiQueryController {

	private final AiQueryService aiQueryService;

	@Operation(summary = "Ask the AI a question about school data", description = "Accepts a natural language question (e.g. \"give me report of this month due and paid fee of this class\"), resolves it to a whitelisted report intent, executes it and returns the structured report with an AI summary. Admin/Superadmin only.")
	@PostMapping("/query")
	public ResponseEntity<ApiResponse<AiQueryResponse>> query(@Valid @RequestBody AiQueryRequest request) {
		log.info(">> ai query: {}", request.getQuestion());
		String email = AuthDetailsProvider.getLoggedEmail();
		return ResponseEntity
				.ok(ApiResponse.success(HttpStatus.OK, "AI query processed", aiQueryService.query(request, email)));
	}
}
