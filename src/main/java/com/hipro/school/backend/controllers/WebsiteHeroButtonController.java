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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hipro.school.backend.dtos.ApiResponse;
import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.dtos.WebsiteHeroButtonRequest;
import com.hipro.school.backend.entities.WebsiteHeroButton;
import com.hipro.school.backend.services.WebsiteHeroButtonService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "Website Hero Button APIs", description = "APIs for managing website hero buttons")
@RestController
@RequestMapping("/v1/website-hero-button")
public class WebsiteHeroButtonController {

	@Autowired
	private WebsiteHeroButtonService service;

	@PostMapping
	@Operation(summary = "Create a new website hero button", description = "Creates a new website hero button for the given hero slide id and returns the created website hero button.")
	public ResponseEntity<ApiResponse<WebsiteHeroButton>> create(@Valid @RequestBody WebsiteHeroButtonRequest request) {
		log.info(">> create({})", request);
		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(HttpStatus.CREATED,
				"Website hero button created successfully", service.create(request)));
	}

	@PutMapping("/{id}")
	@Operation(summary = "Update a website hero button", description = "Updates the website hero button identified by the given id and returns the updated website hero button.")
	public ResponseEntity<ApiResponse<WebsiteHeroButton>> update(@PathVariable Long id,
			@Valid @RequestBody WebsiteHeroButtonRequest request) {
		log.info(">> update({}, {})", id, request);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Website hero button updated successfully",
				service.update(id, request)));
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get a website hero button by id", description = "Fetches the website hero button details for the given id.")
	public ResponseEntity<ApiResponse<WebsiteHeroButton>> getById(@PathVariable Long id) {
		log.info(">> getById({})", id);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Website hero button details fetched successfully",
				service.getEntity(id)));
	}

	@GetMapping
	@Operation(summary = "Get all website hero buttons", description = "Returns a paginated list of website hero buttons filtered by hero slide id.")
	public ResponseEntity<PageResponse<WebsiteHeroButton>> getAll(Pageable pageable, @RequestParam Long heroSlideId) {
		log.info(">> getAll({}, {})", heroSlideId, pageable);
		return ResponseEntity.ok(service.getAll(pageable, heroSlideId));
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Delete a website hero button", description = "Deletes the website hero button identified by the given id.")
	public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
		log.info(">> delete({})", id);
		service.delete(id);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Website hero button deleted successfully", null));
	}

}
