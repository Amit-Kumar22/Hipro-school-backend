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
import com.hipro.school.backend.dtos.SchoolCordinateRequest;
import com.hipro.school.backend.dtos.SchoolRequest;
import com.hipro.school.backend.entities.School;
import com.hipro.school.backend.security.AuthDetailsProvider;
import com.hipro.school.backend.services.SchoolDetailService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/v1/school")
@Tag(name = "School Detail APIs", description = "APIs for fetching and managing school details")
public class SchoolDetailController {

	@Autowired
	private SchoolDetailService schoolService;

	@Operation(summary = "Get all school details")
	@GetMapping
	public ResponseEntity<PageResponse<School>> getAllSchools(Pageable pageable) {
		log.info(">> getAllSchools({})", pageable);
		return ResponseEntity.ok(schoolService.getSchoolDetails(pageable));
	}

	@Operation(summary = "Get LoggedIn school detail.")
	@GetMapping("/detail/{schoolId}")
	public ResponseEntity<School> getSchoolDetail(@PathVariable Long schoolId) {
		log.info(">> getSchoolDetail({})", schoolId);
		return ResponseEntity.ok(schoolService.getSchoolDetailById(schoolId));
	}

	@Operation(summary = "Create or update school details", description = "Creates the school for the logged-in admin. If school details already exist, they are updated - only one school is kept.")
	@PostMapping
	public ResponseEntity<ApiResponse<School>> createSchool(@Valid @RequestBody SchoolRequest request) {
		log.info(">> createSchool({})", request);
		String email = AuthDetailsProvider.getLoggedEmail();
		School saved = schoolService.createSchool(request, email);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "School details saved successfully.", saved));
	}

	@Operation(summary = "Update school details")
	@PutMapping("/{id}")
	public ResponseEntity<School> updateSchool(@PathVariable Long id, @Valid @RequestBody SchoolRequest request) {
		log.info(">> updateSchool({})", id);
		School school = schoolService.updateSchool(id, request);
		return ResponseEntity.ok(school);
	}

	@Operation(summary = "Update school details")
	@PutMapping("/{id}/cordinate")
	public ResponseEntity<School> updateSchoolCordinate(@PathVariable Long id,
			@Valid @RequestBody SchoolCordinateRequest request) {
		log.info(">> updateSchool({})", id);
		schoolService.updateSchoolCordinate(id, request);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@Operation(summary = "Delete school")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteSchool(@PathVariable Long id) {
		log.info(">> deleteSchool({})", id);
		schoolService.deleteSchool(id);
		return ResponseEntity.noContent().build();
	}

}