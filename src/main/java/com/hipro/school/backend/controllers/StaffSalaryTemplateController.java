package com.hipro.school.backend.controllers;

import java.util.List;

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

import com.hipro.school.backend.dtos.StaffSalaryTemplateRequest;
import com.hipro.school.backend.dtos.StaffSalaryTemplateResponse;
import com.hipro.school.backend.services.StaffSalaryTemplateService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Staff Salary Template APIs", description = "APIs for managing the global staff salary template")
@RestController
@RequestMapping("/v1/staff-salary-templates")
@RequiredArgsConstructor
@Slf4j
public class StaffSalaryTemplateController {

	private final StaffSalaryTemplateService templateService;

	@Operation(summary = "Get all staff salary templates", description = "Fetches all salary templates. Only one can be active at a time.")
	@GetMapping
	public ResponseEntity<List<StaffSalaryTemplateResponse>> getAll() {
		return ResponseEntity.ok(templateService.getAll());
	}

	@Operation(summary = "Get active staff salary template", description = "Fetches the currently active salary template used for all staff salary calculations.")
	@GetMapping("/active")
	public ResponseEntity<StaffSalaryTemplateResponse> getActive() {
		return ResponseEntity.ok(templateService.getActive());
	}

	@Operation(summary = "Get a staff salary template by id", description = "Fetches a single salary template by its unique id.")
	@GetMapping("/{id}")
	public ResponseEntity<StaffSalaryTemplateResponse> getById(@PathVariable Long id) {
		log.info("Fetching staff salary template by id: {}", id);
		return ResponseEntity.ok(templateService.getById(id));
	}

	@Operation(summary = "Create a staff salary template", description = "Creates a new salary template. Only one template can be active at a time.")
	@PostMapping
	public ResponseEntity<String> create(@Valid @RequestBody StaffSalaryTemplateRequest request) {
		log.info("Creating new staff salary template: {}", request);
		templateService.create(request);
		return ResponseEntity.status(HttpStatus.CREATED).body("Staff salary template has been created successfully");
	}

	@Operation(summary = "Update a staff salary template", description = "Updates an existing salary template by id.")
	@PutMapping("/{id}")
	public ResponseEntity<String> update(@PathVariable Long id, @Valid @RequestBody StaffSalaryTemplateRequest request) {
		log.info("Updating staff salary template id: {} with request: {}", id, request);
		templateService.update(id, request);
		return ResponseEntity.ok("Staff salary template updated successfully");
	}

	@Operation(summary = "Activate a staff salary template", description = "Activates a template and deactivates the previously active one.")
	@PutMapping("/{id}/activate")
	public ResponseEntity<String> activate(@PathVariable Long id) {
		log.info("Activating staff salary template id: {}", id);
		templateService.activate(id);
		return ResponseEntity.ok("Staff salary template activated successfully");
	}

	@Operation(summary = "Delete a staff salary template", description = "Deletes a salary template. Cannot delete the currently active template.")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		log.info("Deleting staff salary template id: {}", id);
		templateService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
