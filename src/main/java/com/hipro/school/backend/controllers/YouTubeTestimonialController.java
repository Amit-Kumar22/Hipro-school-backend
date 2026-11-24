package com.hipro.school.backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.hipro.school.backend.entities.YouTubeTestimonial;
import com.hipro.school.backend.services.YouTubeTestimonialService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/youtube-testimonial")
@Slf4j
@Tag(name = "YouTube Testimonial Management", description = "APIs to manage YouTube testimonials")
public class YouTubeTestimonialController {

	@Autowired
	private YouTubeTestimonialService service;

	@Operation(summary = "Create a new YouTube testimonial")
	@PostMapping
	public ResponseEntity<ApiResponse<YouTubeTestimonial>> create(@Valid @RequestBody YouTubeTestimonial dto) {
		log.info("API: Creating YouTube Testimonial for video {}", dto.getVideoTitle());
		YouTubeTestimonial created = service.saveYouTubeTestimonial(dto);
		return ResponseEntity
				.ok(ApiResponse.success(HttpStatus.CREATED, "YouTube testimonial created successfully", created));
	}

	@Operation(summary = "Update existing YouTube testimonial by ID")
	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse<YouTubeTestimonial>> update(@PathVariable Long id,
			@Valid @RequestBody YouTubeTestimonial dto) {
		log.info("API: Updating YouTube Testimonial id {}", id);
		YouTubeTestimonial updated = service.updateYouTubeTestimonial(id, dto);
		return ResponseEntity
				.ok(ApiResponse.success(HttpStatus.NO_CONTENT, "YouTube testimonial updated successfully", updated));
	}

	@Operation(summary = "Delete YouTube testimonial by ID")
	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
		log.info("API: Deleting YouTube Testimonial id {}", id);
		service.deleteCountry(id);
		return ResponseEntity
				.ok(ApiResponse.success(HttpStatus.NO_CONTENT, "YouTube testimonial deleted successfully", null));
	}

	@Operation(summary = "Get YouTube testimonial by ID")
	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<YouTubeTestimonial>> getById(@PathVariable Long id) {
		log.info("API: Fetching YouTube Testimonial by id {}", id);
		YouTubeTestimonial testimonial = service.getYouTubeTestimonialById(id);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "YouTube testimonial fetched successfully", testimonial));
	}

	@Operation(summary = "Get all YouTube testimonials")
	@GetMapping
	public ResponseEntity<ApiResponse<List<YouTubeTestimonial>>> getAll() {
		log.info("API: Fetching all YouTube Testimonials");
		return ResponseEntity.ok(service.getAllYouTubeTestimonial());
	}
}
