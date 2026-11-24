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
import com.hipro.school.backend.dtos.WebsiteFloatingSettingRequest;
import com.hipro.school.backend.entities.WebsiteFloatingSetting;
import com.hipro.school.backend.services.WebsiteFloatingSettingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "Website Floating Setting APIs", description = "APIs for managing website floating settings")
@RestController
@RequestMapping("/v1/website-floating-setting")
public class WebsiteFloatingSettingController {

	@Autowired
	private WebsiteFloatingSettingService service;

	@PostMapping
	@Operation(summary = "Create a new website floating setting", description = "Creates a new website floating setting for the given school code and returns the created website floating setting.")
	public ResponseEntity<ApiResponse<WebsiteFloatingSetting>> create(
			@Valid @RequestBody WebsiteFloatingSettingRequest request) {
		log.info(">> create({})", request);
		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(HttpStatus.CREATED,
				"Website floating setting created successfully", service.create(request)));
	}

	@PutMapping("/{id}")
	@Operation(summary = "Update a website floating setting", description = "Updates the website floating setting identified by the given id and returns the updated website floating setting.")
	public ResponseEntity<ApiResponse<WebsiteFloatingSetting>> update(@PathVariable Long id,
			@Valid @RequestBody WebsiteFloatingSettingRequest request) {
		log.info(">> update({}, {})", id, request);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Website floating setting updated successfully",
				service.update(id, request)));
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get a website floating setting by id", description = "Fetches the website floating setting details for the given id.")
	public ResponseEntity<ApiResponse<WebsiteFloatingSetting>> getById(@PathVariable Long id) {
		log.info(">> getById({})", id);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK,
				"Website floating setting details fetched successfully", service.getEntity(id)));
	}

	@GetMapping
	@Operation(summary = "Get all website floating settings", description = "Returns a paginated list of website floating settings.")
	public ResponseEntity<PageResponse<WebsiteFloatingSetting>> getAll(Pageable pageable) {
		log.info(">> getAll({})", pageable);
		return ResponseEntity.ok(service.getAll(pageable));
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Delete a website floating setting", description = "Deletes the website floating setting identified by the given id.")
	public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
		log.info(">> delete({})", id);
		service.delete(id);
		return ResponseEntity
				.ok(ApiResponse.success(HttpStatus.OK, "Website floating setting deleted successfully", null));
	}

}
