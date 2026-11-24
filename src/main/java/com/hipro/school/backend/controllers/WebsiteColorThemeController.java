package com.hipro.school.backend.controllers;

import java.util.List;

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

import com.hipro.school.backend.dtos.ApiResponse;
import com.hipro.school.backend.dtos.ColorThemeRequest;
import com.hipro.school.backend.entities.WebsiteColorTheme;
import com.hipro.school.backend.services.WebsiteColorThemeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/colour-theme")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Color Theme Management", description = "APIs to manage dynamic color themes for UI")
public class WebsiteColorThemeController {

	private final WebsiteColorThemeService service;

	@Operation(summary = "Create a new color theme")
	@PostMapping
	public ResponseEntity<ApiResponse<WebsiteColorTheme>> create(@Valid @RequestBody WebsiteColorTheme dto) {
		log.info("API: Create theme {}", dto.getThemeName());
		service.create(dto);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.CREATED, "Color theme created successfully", null));
	}

	@Operation(summary = "Update existing color theme by ID")
	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse<String>> update(@PathVariable Long id,
			@Valid @RequestBody ColorThemeRequest request) {
		log.info("API: Update theme id {}", id);
		service.update(id, request);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.NO_CONTENT, "Color theme updated successfully", null));
	}

	@Operation(summary = "Delete color theme by ID")
	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
		log.info("API: Delete theme id {}", id);
		service.delete(id);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.NO_CONTENT, "Color theme deleted successfully", null));
	}

	@Operation(summary = "Get color theme by ID")
	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<WebsiteColorTheme>> getById(@PathVariable Long id) {
		log.info("API: Fetch theme by id {}", id);
		WebsiteColorTheme theme = service.getById(id);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Color theme fetched successfully", theme));
	}

	@Operation(summary = "Get all color themes")
	@GetMapping
	public ResponseEntity<ApiResponse<List<WebsiteColorTheme>>> getAll() {
		log.info("API: Fetch all themes");
		List<WebsiteColorTheme> colorThemes = service.getAll();
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Color themes fetched successfully.", colorThemes));
	}

	@Operation(summary = "Get active color themes")
	@GetMapping("/active")
	public ResponseEntity<ApiResponse<WebsiteColorTheme>> getActive() {
		log.info("API: Fetch active themes");
		WebsiteColorTheme res = service.getActiveThemes();
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Active color themes fetched successfully.", res));
	}

	@Operation(summary = " active your  color themes")
	@PutMapping("/activate-color-theme/{colourThemeId}")
	public ResponseEntity<ApiResponse<WebsiteColorTheme>> activateColorTheme(@PathVariable Long colourThemeId) {
		log.info("API: Activate theme id {}", colourThemeId);
		WebsiteColorTheme res = service.activateColorTheme(colourThemeId);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Active color themes fetched successfully.", res));
	}

}
