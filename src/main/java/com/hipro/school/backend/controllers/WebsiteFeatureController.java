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
import com.hipro.school.backend.dtos.WebsiteFeatureRequest;
import com.hipro.school.backend.entities.WebsiteFeature;
import com.hipro.school.backend.services.WebsiteFeatureService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "Website Feature APIs", description = "APIs for managing website features")
@RestController
@RequestMapping("/v1/website-feature")
public class WebsiteFeatureController {

	@Autowired
	private WebsiteFeatureService service;

	@PostMapping
	@Operation(summary = "Create a new website feature", description = "Creates a new website feature for the given school code and returns the created website feature.")
	public ResponseEntity<ApiResponse<WebsiteFeature>> create(@Valid @RequestBody WebsiteFeatureRequest request) {
		log.info(">> create({})", request);
		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(HttpStatus.CREATED,
				"Website feature created successfully", service.create(request)));
	}

	@PutMapping("/{id}")
	@Operation(summary = "Update a website feature", description = "Updates the website feature identified by the given id and returns the updated website feature.")
	public ResponseEntity<ApiResponse<WebsiteFeature>> update(@PathVariable Long id,
			@Valid @RequestBody WebsiteFeatureRequest request) {
		log.info(">> update({}, {})", id, request);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Website feature updated successfully",
				service.update(id, request)));
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get a website feature by id", description = "Fetches the website feature details for the given id.")
	public ResponseEntity<ApiResponse<WebsiteFeature>> getById(@PathVariable Long id) {
		log.info(">> getById({})", id);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Website feature details fetched successfully",
				service.getEntity(id)));
	}

	@GetMapping
	@Operation(summary = "Get all website features", description = "Returns a paginated list of website features.")
	public ResponseEntity<PageResponse<WebsiteFeature>> getAll(Pageable pageable) {
		log.info(">> getAll({})", pageable);
		return ResponseEntity.ok(service.getAll(pageable));
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Delete a website feature", description = "Deletes the website feature identified by the given id.")
	public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
		log.info(">> delete({})", id);
		service.delete(id);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Website feature deleted successfully", null));
	}

}
