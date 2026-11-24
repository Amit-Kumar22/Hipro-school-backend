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
import com.hipro.school.backend.dtos.ClassRequest;
import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.dtos.SchoolClassResponse;
import com.hipro.school.backend.entities.SchoolClass;
import com.hipro.school.backend.services.SchoolClassService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "School Class APIs", description = "APIs for managing school classes")
@RestController
@RequestMapping("/v1/class")
@Slf4j
public class SchoolClassController {

	@Autowired
	private SchoolClassService schoolClassService;

	@Operation(summary = "Get all school classes", description = "Fetches a paginated list of school classes. Supports optional isActive filtering.")
	@GetMapping("/list")
	public ResponseEntity<PageResponse<SchoolClassResponse>> getAllClasses(Pageable pageable) {
		return ResponseEntity.ok(schoolClassService.getAllClasses(pageable));
	}

	@Operation(summary = "Get a school class by id", description = "Fetches a school class by its unique id path parameter.")
	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<SchoolClassResponse>> getClassById(@PathVariable Long id) {
		log.info("Fetching class detail for id: {}", id);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "School class fetched successfully.",
				schoolClassService.getClassById(id)));
	}

	@Operation(summary = "Create a school class", description = "Creates a new school class using the ClassRequest request body.")
	@PostMapping("/save")
	public ResponseEntity<ApiResponse<String>> createClass(@RequestBody ClassRequest request) {
		log.info("Creating new school class: {}", request.getClassName());
		schoolClassService.saveClass(request);
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.success(HttpStatus.CREATED, "School class created successfully.", null));
	}

	@Operation(summary = "Update a school class", description = "Updates an existing school class by id using the school class request body.")
	@PostMapping("/update/{id}")
	public ResponseEntity<ApiResponse<Void>> updateClass(@PathVariable Long id, @RequestBody SchoolClass request) {
		log.info("Updating school class id: {}", id);
		schoolClassService.updateClass(id, request);
		return ResponseEntity
				.ok(ApiResponse.success(HttpStatus.NO_CONTENT, "School class updated successfully.", null));
	}

	@Operation(summary = "Update a school class", description = "Updates an existing school class by id using the school class request body.")
	@PutMapping("/assign-teacher/{classId}")
	public ResponseEntity<ApiResponse<Void>> assignTeacher(@PathVariable Long classId, @RequestParam Long teacherId) {
		log.info("Updating school class id: {}", teacherId);
		schoolClassService.assignTeacher(classId, teacherId);
		return ResponseEntity
				.ok(ApiResponse.success(HttpStatus.NO_CONTENT, "School class teacher updated successfully.", null));
	}

	@Operation(summary = "Delete a school class", description = "Deletes a school class by its id path parameter.")
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<ApiResponse<Void>> deleteClass(@PathVariable Long id) {
		log.info("Deleting school class id: {}", id);
		schoolClassService.deleteClass(id);
		return ResponseEntity
				.ok(ApiResponse.success(HttpStatus.NO_CONTENT, "School class deleted successfully.", null));
	}

}