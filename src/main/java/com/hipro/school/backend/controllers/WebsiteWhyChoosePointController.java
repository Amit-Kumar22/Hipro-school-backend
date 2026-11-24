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
import com.hipro.school.backend.dtos.WebsiteWhyChoosePointRequest;
import com.hipro.school.backend.entities.WebsiteWhyChoosePoint;
import com.hipro.school.backend.services.WebsiteWhyChoosePointService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "Website Why Choose Point APIs", description = "APIs for managing website why choose points")
@RestController
@RequestMapping("/v1/website-why-choose-point")
public class WebsiteWhyChoosePointController {

	@Autowired
	private WebsiteWhyChoosePointService service;

	@PostMapping
	@Operation(summary = "Create a new website why choose point", description = "Creates a new website why choose point for the given school code and returns the created website why choose point.")
	public ResponseEntity<ApiResponse<WebsiteWhyChoosePoint>> create(
			@Valid @RequestBody WebsiteWhyChoosePointRequest request) {
		log.info(">> create({})", request);
		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(HttpStatus.CREATED,
				"Website why choose point created successfully", service.create(request)));
	}

	@PutMapping("/{id}")
	@Operation(summary = "Update a website why choose point", description = "Updates the website why choose point identified by the given id and returns the updated website why choose point.")
	public ResponseEntity<ApiResponse<WebsiteWhyChoosePoint>> update(@PathVariable Long id,
			@Valid @RequestBody WebsiteWhyChoosePointRequest request) {
		log.info(">> update({}, {})", id, request);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Website why choose point updated successfully",
				service.update(id, request)));
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get a website why choose point by id", description = "Fetches the website why choose point details for the given id.")
	public ResponseEntity<ApiResponse<WebsiteWhyChoosePoint>> getById(@PathVariable Long id) {
		log.info(">> getById({})", id);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK,
				"Website why choose point details fetched successfully", service.getEntity(id)));
	}

	@GetMapping
	@Operation(summary = "Get all website why choose points", description = "Returns a paginated list of website why choose points.")
	public ResponseEntity<PageResponse<WebsiteWhyChoosePoint>> getAll(Pageable pageable) {
		log.info(">> getAll({})", pageable);
		return ResponseEntity.ok(service.getAll(pageable));
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Delete a website why choose point", description = "Deletes the website why choose point identified by the given id.")
	public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
		log.info(">> delete({})", id);
		service.delete(id);
		return ResponseEntity
				.ok(ApiResponse.success(HttpStatus.OK, "Website why choose point deleted successfully", null));
	}

}
