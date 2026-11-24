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
import com.hipro.school.backend.dtos.WebsiteTopperRequest;
import com.hipro.school.backend.entities.WebsiteTopper;
import com.hipro.school.backend.services.WebsiteTopperService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "Website Topper APIs", description = "APIs for managing website toppers")
@RestController
@RequestMapping("/v1/website-topper")
public class WebsiteTopperController {

	@Autowired
	private WebsiteTopperService service;

	@PostMapping
	@Operation(summary = "Create a new website topper", description = "Creates a new website topper for the given school code and returns the created website topper.")
	public ResponseEntity<ApiResponse<WebsiteTopper>> create(@Valid @RequestBody WebsiteTopperRequest request) {
		log.info(">> create({})", request);
		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(HttpStatus.CREATED,
				"Website topper created successfully", service.create(request)));
	}

	@PutMapping("/{id}")
	@Operation(summary = "Update a website topper", description = "Updates the website topper identified by the given id and returns the updated website topper.")
	public ResponseEntity<ApiResponse<WebsiteTopper>> update(@PathVariable Long id,
			@Valid @RequestBody WebsiteTopperRequest request) {
		log.info(">> update({}, {})", id, request);
		return ResponseEntity.ok(
				ApiResponse.success(HttpStatus.OK, "Website topper updated successfully", service.update(id, request)));
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get a website topper by id", description = "Fetches the website topper details for the given id.")
	public ResponseEntity<ApiResponse<WebsiteTopper>> getById(@PathVariable Long id) {
		log.info(">> getById({})", id);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Website topper details fetched successfully",
				service.getEntity(id)));
	}

	@GetMapping
	@Operation(summary = "Get all website toppers", description = "Returns a paginated list of website toppers.")
	public ResponseEntity<PageResponse<WebsiteTopper>> getAll(Pageable pageable) {
		log.info(">> getAll({})", pageable);
		return ResponseEntity.ok(service.getAll(pageable));
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Delete a website topper", description = "Deletes the website topper identified by the given id.")
	public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
		log.info(">> delete({})", id);
		service.delete(id);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Website topper deleted successfully", null));
	}

}
