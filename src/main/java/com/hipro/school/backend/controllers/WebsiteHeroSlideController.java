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
import com.hipro.school.backend.dtos.WebsiteHeroSlideRequest;
import com.hipro.school.backend.entities.WebsiteHeroSlide;
import com.hipro.school.backend.services.WebsiteHeroSlideService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "Website Hero Slide APIs", description = "APIs for managing website hero slides")
@RestController
@RequestMapping("/v1/website-hero-slide")
public class WebsiteHeroSlideController {

	@Autowired
	private WebsiteHeroSlideService service;

	@PostMapping
	@Operation(summary = "Create a new website hero slide", description = "Creates a new website hero slide for the given school code and returns the created website hero slide.")
	public ResponseEntity<ApiResponse<WebsiteHeroSlide>> create(@Valid @RequestBody WebsiteHeroSlideRequest request) {
		log.info(">> create({})", request);
		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(HttpStatus.CREATED,
				"Website hero slide created successfully", service.create(request)));
	}

	@PutMapping("/{id}")
	@Operation(summary = "Update a website hero slide", description = "Updates the website hero slide identified by the given id and returns the updated website hero slide.")
	public ResponseEntity<ApiResponse<WebsiteHeroSlide>> update(@PathVariable Long id,
			@Valid @RequestBody WebsiteHeroSlideRequest request) {
		log.info(">> update({}, {})", id, request);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Website hero slide updated successfully",
				service.update(id, request)));
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get a website hero slide by id", description = "Fetches the website hero slide details for the given id.")
	public ResponseEntity<ApiResponse<WebsiteHeroSlide>> getById(@PathVariable Long id) {
		log.info(">> getById({})", id);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Website hero slide details fetched successfully",
				service.getEntity(id)));
	}

	@GetMapping
	@Operation(summary = "Get all website hero slides", description = "Returns a paginated list of website hero slides.")
	public ResponseEntity<PageResponse<WebsiteHeroSlide>> getAll(Pageable pageable) {
		log.info(">> getAll({})", pageable);
		return ResponseEntity.ok(service.getAll(pageable));
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Delete a website hero slide", description = "Deletes the website hero slide identified by the given id.")
	public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
		log.info(">> delete({})", id);
		service.delete(id);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Website hero slide deleted successfully", null));
	}

}
