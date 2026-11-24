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
import com.hipro.school.backend.dtos.WebsiteContactRequest;
import com.hipro.school.backend.entities.WebsiteContact;
import com.hipro.school.backend.services.WebsiteContactService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "Website Contact APIs", description = "APIs for managing website contacts")
@RestController
@RequestMapping("/v1/website-contact")
public class WebsiteContactController {

	@Autowired
	private WebsiteContactService service;

	@PostMapping
	@Operation(summary = "Create a new website contact", description = "Creates a new website contact for the given school code and returns the created website contact.")
	public ResponseEntity<ApiResponse<WebsiteContact>> create(@Valid @RequestBody WebsiteContactRequest request) {
		log.info(">> create({})", request);
		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(HttpStatus.CREATED,
				"Website contact created successfully", service.create(request)));
	}

	@PutMapping("/{id}")
	@Operation(summary = "Update a website contact", description = "Updates the website contact identified by the given id and returns the updated website contact.")
	public ResponseEntity<ApiResponse<WebsiteContact>> update(@PathVariable Long id,
			@Valid @RequestBody WebsiteContactRequest request) {
		log.info(">> update({}, {})", id, request);
		return ResponseEntity.ok(
				ApiResponse.success(HttpStatus.OK, "Website contact updated successfully", service.update(id, request)));
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get a website contact by id", description = "Fetches the website contact details for the given id.")
	public ResponseEntity<ApiResponse<WebsiteContact>> getById(@PathVariable Long id) {
		log.info(">> getById({})", id);
		return ResponseEntity.ok(
				ApiResponse.success(HttpStatus.OK, "Website contact details fetched successfully", service.getEntity(id)));
	}

	@GetMapping
	@Operation(summary = "Get all website contacts", description = "Returns a paginated list of website contacts.")
	public ResponseEntity<PageResponse<WebsiteContact>> getAll(Pageable pageable) {
		log.info(">> getAll({})", pageable);
		return ResponseEntity.ok(service.getAll(pageable));
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Delete a website contact", description = "Deletes the website contact identified by the given id.")
	public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
		log.info(">> delete({})", id);
		service.delete(id);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Website contact deleted successfully", null));
	}

}
