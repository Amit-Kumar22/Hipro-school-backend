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
import com.hipro.school.backend.dtos.WebsiteTestimonialRequest;
import com.hipro.school.backend.entities.WebsiteTestimonial;
import com.hipro.school.backend.services.WebsiteTestimonialService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "Website Testimonial APIs", description = "APIs for managing website testimonials")
@RestController
@RequestMapping("/v1/website-testimonial")
public class WebsiteTestimonialController {

	@Autowired
	private WebsiteTestimonialService service;

	@PostMapping
	@Operation(summary = "Create a new website testimonial", description = "Creates a new website testimonial for the given school code and returns the created website testimonial.")
	public ResponseEntity<ApiResponse<WebsiteTestimonial>> create(
			@Valid @RequestBody WebsiteTestimonialRequest request) {
		log.info(">> create({})", request);
		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(HttpStatus.CREATED,
				"Website testimonial created successfully", service.create(request)));
	}

	@PutMapping("/{id}")
	@Operation(summary = "Update a website testimonial", description = "Updates the website testimonial identified by the given id and returns the updated website testimonial.")
	public ResponseEntity<ApiResponse<WebsiteTestimonial>> update(@PathVariable Long id,
			@Valid @RequestBody WebsiteTestimonialRequest request) {
		log.info(">> update({}, {})", id, request);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Website testimonial updated successfully",
				service.update(id, request)));
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get a website testimonial by id", description = "Fetches the website testimonial details for the given id.")
	public ResponseEntity<ApiResponse<WebsiteTestimonial>> getById(@PathVariable Long id) {
		log.info(">> getById({})", id);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Website testimonial details fetched successfully",
				service.getEntity(id)));
	}

	@GetMapping
	@Operation(summary = "Get all website testimonials", description = "Returns a paginated list of website testimonials.")
	public ResponseEntity<PageResponse<WebsiteTestimonial>> getAll(Pageable pageable) {
		log.info(">> getAll({})", pageable);
		return ResponseEntity.ok(service.getAll(pageable));
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Delete a website testimonial", description = "Deletes the website testimonial identified by the given id.")
	public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
		log.info(">> delete({})", id);
		service.delete(id);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Website testimonial deleted successfully", null));
	}

}
