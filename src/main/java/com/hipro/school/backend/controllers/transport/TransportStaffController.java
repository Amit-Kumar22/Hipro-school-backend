package com.hipro.school.backend.controllers.transport;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.dtos.transport.TransportStaffRequest;
import com.hipro.school.backend.dtos.transport.TransportStaffResponse;
import com.hipro.school.backend.enums.TransportDesignation;
import com.hipro.school.backend.enums.TransportEmploymentStatus;
import com.hipro.school.backend.services.transport.TransportStaffService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Transport Staff APIs", description = "APIs for managing drivers, conductors and transport in-charges")
@RestController
@RequestMapping("/v1/transport/staff")
@RequiredArgsConstructor
@Slf4j
public class TransportStaffController {

	private final TransportStaffService staffService;

	@Operation(summary = "List transport staff", description = "Paginated staff list with optional designation, employment status and name/mobile search filters.")
	@GetMapping
	public ResponseEntity<PageResponse<TransportStaffResponse>> getAll(
			@RequestParam(required = false) TransportDesignation designation,
			@RequestParam(required = false) TransportEmploymentStatus employmentStatus,
			@RequestParam(required = false) String search, Pageable pageable) {
		return ResponseEntity.ok(staffService.getAll(designation, employmentStatus, search, pageable));
	}

	@Operation(summary = "Get transport staff by id", description = "Fetches a single transport staff member by id.")
	@GetMapping("/{id}")
	public ResponseEntity<TransportStaffResponse> getById(@PathVariable Long id) {
		return ResponseEntity.ok(staffService.getById(id));
	}

	@Operation(summary = "Add transport staff", description = "Registers a driver, conductor or transport in-charge. Drivers with expired licences are rejected.")
	@PostMapping
	public ResponseEntity<TransportStaffResponse> create(@Valid @RequestBody TransportStaffRequest request) {
		log.info("Creating transport staff {}", request.getName());
		return ResponseEntity.status(HttpStatus.CREATED).body(staffService.create(request));
	}

	@Operation(summary = "Update transport staff", description = "Updates a transport staff member's details.")
	@PutMapping("/{id}")
	public ResponseEntity<TransportStaffResponse> update(@PathVariable Long id,
			@Valid @RequestBody TransportStaffRequest request) {
		return ResponseEntity.ok(staffService.update(id, request));
	}

	@Operation(summary = "Assign vehicle", description = "Assigns a vehicle to a staff member. Drivers with expired licences cannot be assigned.")
	@PutMapping("/{id}/assign-vehicle")
	public ResponseEntity<TransportStaffResponse> assignVehicle(@PathVariable Long id,
			@RequestParam Long vehicleId) {
		return ResponseEntity.ok(staffService.assignVehicle(id, vehicleId));
	}

	@Operation(summary = "Unassign vehicle", description = "Removes the vehicle assignment from a staff member.")
	@PutMapping("/{id}/unassign-vehicle")
	public ResponseEntity<String> unassignVehicle(@PathVariable Long id) {
		staffService.unassignVehicle(id);
		return ResponseEntity.ok("Vehicle unassigned successfully");
	}

	@Operation(summary = "Deactivate staff", description = "Marks a transport staff member as relieved (soft deactivate).")
	@PutMapping("/{id}/deactivate")
	public ResponseEntity<String> deactivate(@PathVariable Long id) {
		staffService.deactivate(id);
		return ResponseEntity.ok("Transport staff deactivated successfully");
	}
}
