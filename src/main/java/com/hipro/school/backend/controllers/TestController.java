package com.hipro.school.backend.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Health Check APIs", description = "Health check endpoint")
@RestController
@RequestMapping("/health")
@Slf4j
public class TestController {

	@Operation(summary = "Health check", description = "Checks the health status of the application.")
	@GetMapping
	public ResponseEntity<Map<String, String>> healthCheck() {
		log.info("Health check requested");
		Map<String, String> status = new HashMap<>();
		status.put("status", "UP");
		return ResponseEntity.ok(status);
	}

}