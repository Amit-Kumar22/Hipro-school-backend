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
import com.hipro.school.backend.dtos.WebsiteNavigationItemRequest;
import com.hipro.school.backend.entities.WebsiteNavigationItem;
import com.hipro.school.backend.services.WebsiteNavigationItemService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "Website Navigation Item APIs", description = "APIs for managing website navigation items")
@RestController
@RequestMapping("/v1/website-navigation-item")
public class WebsiteNavigationItemController {

	@Autowired
	private WebsiteNavigationItemService service;

	@PostMapping
	@Operation(summary = "Create a new website navigation item", description = "Creates a new website navigation item for the given school code and returns the created website navigation item.")
	public ResponseEntity<ApiResponse<WebsiteNavigationItem>> create(
			@Valid @RequestBody WebsiteNavigationItemRequest request) {
		log.info(">> create({})", request);
		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(HttpStatus.CREATED,
				"Website navigation item created successfully", service.create(request)));
	}

	@PutMapping("/{id}")
	@Operation(summary = "Update a website navigation item", description = "Updates the website navigation item identified by the given id and returns the updated website navigation item.")
	public ResponseEntity<ApiResponse<WebsiteNavigationItem>> update(@PathVariable Long id,
			@Valid @RequestBody WebsiteNavigationItemRequest request) {
		log.info(">> update({}, {})", id, request);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Website navigation item updated successfully",
				service.update(id, request)));
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get a website navigation item by id", description = "Fetches the website navigation item details for the given id.")
	public ResponseEntity<ApiResponse<WebsiteNavigationItem>> getById(@PathVariable Long id) {
		log.info(">> getById({})", id);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK,
				"Website navigation item details fetched successfully", service.getEntity(id)));
	}

	@GetMapping
	@Operation(summary = "Get all website navigation items", description = "Returns a paginated list of website navigation items.")
	public ResponseEntity<PageResponse<WebsiteNavigationItem>> getAll(Pageable pageable) {
		log.info(">> getAll({})", pageable);
		return ResponseEntity.ok(service.getAll(pageable));
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Delete a website navigation item", description = "Deletes the website navigation item identified by the given id.")
	public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
		log.info(">> delete({})", id);
		service.delete(id);
		return ResponseEntity
				.ok(ApiResponse.success(HttpStatus.OK, "Website navigation item deleted successfully", null));
	}

}
