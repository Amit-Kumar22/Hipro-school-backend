package com.hipro.school.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.entities.AboutUs;
import com.hipro.school.backend.services.AboutUsService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "About Us APIs", description = "APIs for managing About Us content")
@RestController
@RequestMapping("/v1/about-us")
@Slf4j
public class AboutUsController {

	@Autowired
	private AboutUsService aboutUsService;

	@Operation(summary = "Create About Us content", description = "Creates new About Us content using the AboutUs request body.")
	@PostMapping
	public ResponseEntity<AboutUs> create(@RequestBody AboutUs aboutUs) {
		log.info("Creating about us: {}", aboutUs);
		return ResponseEntity.ok(aboutUsService.createAboutUs(aboutUs));
	}

	@Operation(summary = "Update About Us content", description = "Updates About Us content by id using the AboutUs request body.")
	@PutMapping("/{id}")
	public ResponseEntity<AboutUs> update(@PathVariable Long id, @RequestBody AboutUs aboutUs) {
		log.info("Updating about us id: {} with about us: {}", id, aboutUs);
		return ResponseEntity.ok(aboutUsService.updateAboutUs(id, aboutUs));
	}

	@Operation(summary = "Get About Us content by id", description = "Fetches About Us content by its unique id path parameter.")
	@GetMapping("/{id}")
	public ResponseEntity<AboutUs> getById(@PathVariable Long id) {
		log.info("Fetching about us by id: {}", id);
		return ResponseEntity.ok(aboutUsService.getAboutUs(id));
	}

	@Operation(summary = "Get all About Us content", description = "Fetches a paginated list of About Us content.")
	@GetMapping
	public ResponseEntity<PageResponse<AboutUs>> getAll(Pageable pageable) {
		log.info("Fetching all about us with pageable: {}", pageable);
		return ResponseEntity.ok(aboutUsService.getAllAboutUs(pageable));
	}

	@Operation(summary = "Delete About Us content", description = "Deletes About Us content by its id path parameter.")
	@DeleteMapping("/{id}")
	public ResponseEntity<String> delete(@PathVariable Long id) {
		log.info("Deleting about us id: {}", id);
		aboutUsService.deleteAboutUs(id);
		return ResponseEntity.ok("AboutUs deleted successfully");
	}

}