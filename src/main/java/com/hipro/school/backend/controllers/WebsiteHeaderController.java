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
import com.hipro.school.backend.dtos.WebsiteHeaderRequest;
import com.hipro.school.backend.entities.WebsiteHeader;
import com.hipro.school.backend.services.WebsiteHeaderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "Website Header APIs", description = "APIs for managing website headers")
@RestController
@RequestMapping("/v1/website-header")
public class WebsiteHeaderController {

	@Autowired
	private WebsiteHeaderService service;

	@PostMapping
	@Operation(summary = "Create a new website header", description = "Creates a new website header for the given school code and returns the created website header.")
	public ResponseEntity<ApiResponse<WebsiteHeader>> create(@Valid @RequestBody WebsiteHeaderRequest request) {
		log.info(">> create({})", request);
		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(HttpStatus.CREATED,
				"Website header created successfully", service.create(request)));
	}

	@PutMapping("/{id}")
	@Operation(summary = "Update a website header", description = "Updates the website header identified by the given id and returns the updated website header.")
	public ResponseEntity<ApiResponse<WebsiteHeader>> update(@PathVariable Long id,
			@Valid @RequestBody WebsiteHeaderRequest request) {
		log.info(">> update({}, {})", id, request);
		return ResponseEntity.ok(
				ApiResponse.success(HttpStatus.OK, "Website header updated successfully", service.update(id, request)));
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get a website header by id", description = "Fetches the website header details for the given id.")
	public ResponseEntity<ApiResponse<WebsiteHeader>> getById(@PathVariable Long id) {
		log.info(">> getById({})", id);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Website header details fetched successfully",
				service.getEntity(id)));
	}

	@GetMapping
	@Operation(summary = "Get all website headers", description = "Returns a paginated list of website headers.")
	public ResponseEntity<PageResponse<WebsiteHeader>> getAll(Pageable pageable) {
		log.info(">> getAll({})", pageable);
		return ResponseEntity.ok(service.getAll(pageable));
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Delete a website header", description = "Deletes the website header identified by the given id.")
	public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
		log.info(">> delete({})", id);
		service.delete(id);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Website header deleted successfully", null));
	}

}
