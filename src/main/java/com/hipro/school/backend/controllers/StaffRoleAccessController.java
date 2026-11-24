package com.hipro.school.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.dtos.StaffAccessControlResponse;
import com.hipro.school.backend.dtos.StaffPermissionRequest;
import com.hipro.school.backend.services.StaffService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "Staff Permission APIs", description = "APIs for managing staff access and permissions")
@RestController
@RequestMapping("/v1/staff-permissions")
public class StaffRoleAccessController {

	@Autowired
	private StaffService staffService;

	@Operation(summary = "Get all Staff Detail Permissions for admin")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successfully retrieved All Staff Permissions") })
	@GetMapping
	public ResponseEntity<PageResponse<StaffAccessControlResponse>> getAllStaffPermissions(Pageable pageable) {
		log.info(">> getAllStaff({})", pageable);
		return ResponseEntity.ok(staffService.getAllStaffPermission(pageable));
	}

	@Operation(summary = "Get Staff Detail by ID")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Demo found"),
			@ApiResponse(responseCode = "404", description = "FAQs not found") })
	@GetMapping("/{id}")
	public ResponseEntity<StaffAccessControlResponse> getStaffByPermissionId(@PathVariable Long id) {
		log.info(">> getStaffByPermissionId({})", id);
		return ResponseEntity.ok(staffService.getStaffByPermissionId(id));
	}

	@Operation(summary = "Add a new AStaff Detail credential")
	@ApiResponses(value = { @ApiResponse(responseCode = "201", description = "Demo created successfully"),
			@ApiResponse(responseCode = "400", description = "Invalid request data") })
	@PostMapping("/add")
	public ResponseEntity<Void> addStaffPermissions(@Valid @RequestBody StaffPermissionRequest req) {
		log.info(">> addStaffPermissions({})", req);
		staffService.addStaffPermissions(req);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@Operation(summary = "Update an existing Staff Detail")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "FAQs updated successfully"),
			@ApiResponse(responseCode = "400", description = "Invalid request data"),
			@ApiResponse(responseCode = "404", description = "Demo not found") })
	@PutMapping("/update")
	public ResponseEntity<Void> updateStaffPermissions(@Valid @RequestBody StaffPermissionRequest req) {
		log.info(">> updateStaffPermissions({})", req);
		staffService.updateStaffPermissions(req);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

}
