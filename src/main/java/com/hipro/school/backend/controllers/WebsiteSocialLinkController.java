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
import com.hipro.school.backend.dtos.WebsiteSocialLinkRequest;
import com.hipro.school.backend.entities.WebsiteSocialLink;
import com.hipro.school.backend.services.WebsiteSocialLinkService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "Website Social Link APIs", description = "APIs for managing website social links")
@RestController
@RequestMapping("/v1/website-social-link")
public class WebsiteSocialLinkController {

	@Autowired
	private WebsiteSocialLinkService service;

	@PostMapping
	@Operation(summary = "Create a new website social link", description = "Creates a new website social link for the given school code and returns the created website social link.")
	public ResponseEntity<ApiResponse<WebsiteSocialLink>> create(@Valid @RequestBody WebsiteSocialLinkRequest request) {
		log.info(">> create({})", request);
		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(HttpStatus.CREATED,
				"Website social link created successfully", service.create(request)));
	}

	@PutMapping("/{id}")
	@Operation(summary = "Update a website social link", description = "Updates the website social link identified by the given id and returns the updated website social link.")
	public ResponseEntity<ApiResponse<WebsiteSocialLink>> update(@PathVariable Long id,
			@Valid @RequestBody WebsiteSocialLinkRequest request) {
		log.info(">> update({}, {})", id, request);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Website social link updated successfully",
				service.update(id, request)));
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get a website social link by id", description = "Fetches the website social link details for the given id.")
	public ResponseEntity<ApiResponse<WebsiteSocialLink>> getById(@PathVariable Long id) {
		log.info(">> getById({})", id);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Website social link details fetched successfully",
				service.getEntity(id)));
	}

	@GetMapping
	@Operation(summary = "Get all website social links", description = "Returns a paginated list of website social links.")
	public ResponseEntity<PageResponse<WebsiteSocialLink>> getAll(Pageable pageable) {
		log.info(">> getAll({})", pageable);
		return ResponseEntity.ok(service.getAll(pageable));
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Delete a website social link", description = "Deletes the website social link identified by the given id.")
	public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
		log.info(">> delete({})", id);
		service.delete(id);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Website social link deleted successfully", null));
	}

}
