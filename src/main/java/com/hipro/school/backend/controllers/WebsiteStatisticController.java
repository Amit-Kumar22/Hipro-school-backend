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
import com.hipro.school.backend.dtos.WebsiteStatisticRequest;
import com.hipro.school.backend.entities.WebsiteStatistic;
import com.hipro.school.backend.services.WebsiteStatisticService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "Website Statistic APIs", description = "APIs for managing website statistics")
@RestController
@RequestMapping("/v1/website-statistic")
public class WebsiteStatisticController {

	@Autowired
	private WebsiteStatisticService service;

	@PostMapping
	@Operation(summary = "Create a new website statistic", description = "Creates a new website statistic for the given school code and returns the created website statistic.")
	public ResponseEntity<ApiResponse<WebsiteStatistic>> create(@Valid @RequestBody WebsiteStatisticRequest request) {
		log.info(">> create({})", request);
		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(HttpStatus.CREATED,
				"Website statistic created successfully", service.create(request)));
	}

	@PutMapping("/{id}")
	@Operation(summary = "Update a website statistic", description = "Updates the website statistic identified by the given id and returns the updated website statistic.")
	public ResponseEntity<ApiResponse<WebsiteStatistic>> update(@PathVariable Long id,
			@Valid @RequestBody WebsiteStatisticRequest request) {
		log.info(">> update({}, {})", id, request);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Website statistic updated successfully",
				service.update(id, request)));
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get a website statistic by id", description = "Fetches the website statistic details for the given id.")
	public ResponseEntity<ApiResponse<WebsiteStatistic>> getById(@PathVariable Long id) {
		log.info(">> getById({})", id);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Website statistic details fetched successfully",
				service.getEntity(id)));
	}

	@GetMapping
	@Operation(summary = "Get all website statistics", description = "Returns a paginated list of website statistics.")
	public ResponseEntity<PageResponse<WebsiteStatistic>> getAll(Pageable pageable) {
		log.info(">> getAll({})", pageable);
		return ResponseEntity.ok(service.getAll(pageable));
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Delete a website statistic", description = "Deletes the website statistic identified by the given id.")
	public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
		log.info(">> delete({})", id);
		service.delete(id);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Website statistic deleted successfully", null));
	}

}
