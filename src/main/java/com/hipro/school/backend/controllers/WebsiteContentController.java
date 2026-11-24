package com.hipro.school.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hipro.school.backend.dtos.ApiResponse;
import com.hipro.school.backend.dtos.WebsiteContentResponse;
import com.hipro.school.backend.services.WebsiteContentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "Website Content APIs", description = "APIs for fetching aggregated public website content")
@RestController
@RequestMapping("/v1/website-content")
public class WebsiteContentController {

	@Autowired
	private WebsiteContentService service;

	@GetMapping
	@Operation(summary = "Get school website content", description = "Returns the complete website content used to render the public school website.")
	public ResponseEntity<ApiResponse<WebsiteContentResponse>> getWebsiteContent() {
		log.info(">> getWebsiteContent()");
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Website content fetched successfully",
				service.getWebsiteContent()));
	}

}
