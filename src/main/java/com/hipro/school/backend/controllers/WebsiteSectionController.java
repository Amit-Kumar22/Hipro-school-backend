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
import com.hipro.school.backend.dtos.WebsiteSectionRequest;
import com.hipro.school.backend.entities.WebsiteSection;
import com.hipro.school.backend.services.WebsiteSectionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "Website Section APIs", description = "APIs for managing website sections")
@RestController
@RequestMapping("/v1/website-section")
public class WebsiteSectionController {

	@Autowired
	private WebsiteSectionService service;

	@PostMapping
	@Operation(summary = "Create a new website section", description = "Creates a new website section for the given school code and returns the created website section.")
	public ResponseEntity<ApiResponse<WebsiteSection>> create(@Valid @RequestBody WebsiteSectionRequest request) {
		log.info(">> create({})", request);
		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(HttpStatus.CREATED,
				"Website section created successfully", service.create(request)));
	}

	@PutMapping("/{id}")
	@Operation(summary = "Update a website section", description = "Updates the website section identified by the given id and returns the updated website section.")
	public ResponseEntity<ApiResponse<WebsiteSection>> update(@PathVariable Long id,
			@Valid @RequestBody WebsiteSectionRequest request) {
		log.info(">> update({}, {})", id, request);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Website section updated successfully",
				service.update(id, request)));
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get a website section by id", description = "Fetches the website section details for the given id.")
	public ResponseEntity<ApiResponse<WebsiteSection>> getById(@PathVariable Long id) {
		log.info(">> getById({})", id);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Website section details fetched successfully",
				service.getEntity(id)));
	}

	@GetMapping
	@Operation(summary = "Get all website sections", description = "Returns a paginated list of website sections.")
	public ResponseEntity<PageResponse<WebsiteSection>> getAll(Pageable pageable) {
		log.info(">> getAll({})", pageable);
		return ResponseEntity.ok(service.getAll(pageable));
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Delete a website section", description = "Deletes the website section identified by the given id.")
	public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
		log.info(">> delete({})", id);
		service.delete(id);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Website section deleted successfully", null));
	}

}
