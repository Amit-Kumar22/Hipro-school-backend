package com.hipro.school.backend.controllers;

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
import org.springframework.web.bind.annotation.RestController;

import com.hipro.school.backend.dtos.ApiResponse;
import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.dtos.WebsiteSettingRequest;
import com.hipro.school.backend.entities.WebsiteSetting;
import com.hipro.school.backend.services.WebsiteSettingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "Website Setting APIs", description = "APIs for managing website settings")
@RestController
@RequestMapping("/v1/website-setting")
public class WebsiteSettingController {

	@Autowired
	private WebsiteSettingService service;

	@PostMapping
	@Operation(summary = "Create a new website setting", description = "Creates a new website setting for the given school code and returns the created website setting.")
	public ResponseEntity<ApiResponse<WebsiteSetting>> create(@Valid @RequestBody WebsiteSettingRequest request) {
		log.info(">> create({})", request);
		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(HttpStatus.CREATED,
				"Website setting created successfully", service.create(request)));
	}

	@PutMapping("/{id}")
	@Operation(summary = "Update a website setting", description = "Updates the website setting identified by the given id and returns the updated website setting.")
	public ResponseEntity<ApiResponse<WebsiteSetting>> update(@PathVariable Long id,
			@Valid @RequestBody WebsiteSettingRequest request) {
		log.info(">> update({}, {})", id, request);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Website setting updated successfully",
				service.update(id, request)));
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get a website setting by id", description = "Fetches the website setting details for the given id.")
	public ResponseEntity<ApiResponse<WebsiteSetting>> getById(@PathVariable Long id) {
		log.info(">> getById({})", id);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Website setting details fetched successfully",
				service.getEntity(id)));
	}

	@GetMapping
	@Operation(summary = "Get all website settings", description = "Returns a paginated list of website settings.")
	public ResponseEntity<PageResponse<WebsiteSetting>> getAll(Pageable pageable) {
		log.info(">> getAll({})", pageable);
		return ResponseEntity.ok(service.getAll(pageable));
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Delete a website setting", description = "Deletes the website setting identified by the given id.")
	public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
		log.info(">> delete({})", id);
		service.delete(id);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Website setting deleted successfully", null));
	}

}
