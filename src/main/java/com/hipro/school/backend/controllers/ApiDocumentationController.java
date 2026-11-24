package com.hipro.school.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.hipro.school.backend.services.ApiDocumentationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "API Documentation APIs", description = "APIs to fetch, generate and download API documentation")
@RestController
@RequestMapping("/v1/documentation")
@Slf4j
public class ApiDocumentationController {

	@Autowired
	private ApiDocumentationService apiDocumentationService;

	@Operation(summary = "Get OpenAPI document", description = "Fetches the raw OpenAPI JSON document.")
	@GetMapping("/openapi")
	public JsonNode getOpenApi() {
		log.info("Fetching OpenAPI document");
		return apiDocumentationService.getOpenApiDocument();
	}

	@Operation(summary = "Generate API documentation", description = "Triggers generation of the API documentation.")
	@PostMapping("/generate")
	public ResponseEntity<String> generateDocumentation() {
		log.info("Generating API documentation");
		//apiDocumentationService.generateDocumentation();
		return ResponseEntity.ok("API documentation generated successfully");
	}

	@Operation(summary = "Download API documentation", description = "Generates and downloads the API documentation as a zip file.")
	@GetMapping("/download")
	public ResponseEntity<byte[]> downloadDocumentation() {
		log.info("Downloading API documentation");

		byte[] zipBytes = apiDocumentationService.generateAndZipDocumentation();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		headers.setContentDisposition(ContentDisposition.attachment().filename("school-api-documentation.zip").build());
		headers.setContentLength(zipBytes.length);

		return ResponseEntity.ok().headers(headers).body(zipBytes);
	}
}