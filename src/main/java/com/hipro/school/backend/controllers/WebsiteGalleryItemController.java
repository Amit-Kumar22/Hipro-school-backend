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
import com.hipro.school.backend.dtos.WebsiteGalleryItemRequest;
import com.hipro.school.backend.entities.WebsiteGalleryItem;
import com.hipro.school.backend.services.WebsiteGalleryItemService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "Website Gallery Item APIs", description = "APIs for managing website gallery items")
@RestController
@RequestMapping("/v1/website-gallery-item")
public class WebsiteGalleryItemController {

	@Autowired
	private WebsiteGalleryItemService service;

	@PostMapping
	@Operation(summary = "Create a new website gallery item", description = "Creates a new website gallery item for the given school code and returns the created website gallery item.")
	public ResponseEntity<ApiResponse<WebsiteGalleryItem>> create(
			@Valid @RequestBody WebsiteGalleryItemRequest request) {
		log.info(">> create({})", request);
		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(HttpStatus.CREATED,
				"Website gallery item created successfully", service.create(request)));
	}

	@PutMapping("/{id}")
	@Operation(summary = "Update a website gallery item", description = "Updates the website gallery item identified by the given id and returns the updated website gallery item.")
	public ResponseEntity<ApiResponse<WebsiteGalleryItem>> update(@PathVariable Long id,
			@Valid @RequestBody WebsiteGalleryItemRequest request) {
		log.info(">> update({}, {})", id, request);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Website gallery item updated successfully",
				service.update(id, request)));
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get a website gallery item by id", description = "Fetches the website gallery item details for the given id.")
	public ResponseEntity<ApiResponse<WebsiteGalleryItem>> getById(@PathVariable Long id) {
		log.info(">> getById({})", id);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Website gallery item details fetched successfully",
				service.getEntity(id)));
	}

	@GetMapping
	@Operation(summary = "Get all website gallery items", description = "Returns a paginated list of website gallery items.")
	public ResponseEntity<PageResponse<WebsiteGalleryItem>> getAll(Pageable pageable) {
		log.info(">> getAll({})", pageable);
		return ResponseEntity.ok(service.getAll(pageable));
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Delete a website gallery item", description = "Deletes the website gallery item identified by the given id.")
	public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
		log.info(">> delete({})", id);
		service.delete(id);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Website gallery item deleted successfully", null));
	}

}
