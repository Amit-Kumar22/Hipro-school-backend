package com.hipro.school.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hipro.school.backend.dtos.ApiResponse;
import com.hipro.school.backend.dtos.CustomerResponse;
import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.dtos.StaffUserRequest;
import com.hipro.school.backend.dtos.transport.DriverRequest;
import com.hipro.school.backend.dtos.transport.DriverResponse;
import com.hipro.school.backend.entities.Role;
import com.hipro.school.backend.entities.Student;
import com.hipro.school.backend.entities.Teacher;
import com.hipro.school.backend.security.AuthDetailsProvider;
import com.hipro.school.backend.services.ProfileService;
import com.hipro.school.backend.services.transport.TransportStaffService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "Admin User APIs", description = "This collection contains APIs for Admin resources")
@RestController
@RequestMapping("/v1/admin")
public class AdminController {

	@Autowired
	private ProfileService profileService;

	@Autowired
	private TransportStaffService transportStaffService;

	@Operation(summary = "Get All Staff Details API", description = "This API fetches All Staff Details.")
	@GetMapping("/all-staff")
	public ResponseEntity<PageResponse<CustomerResponse>> getAllStaffDetails(Pageable pageable,
			@RequestParam(required = false) Role role, @RequestParam(required = false) String search) {
		log.info(">> getAllStaffDetails({}, {}, {}, {})", pageable, role, search);
		String email = AuthDetailsProvider.getLoggedEmail();
		return ResponseEntity.ok(profileService.getAllSchoolStaff(email, role, search, pageable));
	}

	@Operation(summary = "Get All Staff Details API", description = "This API fetches All Staff Details.")
	@GetMapping("/all-teacher")
	public ResponseEntity<PageResponse<Teacher>> getAllTeacher(Pageable pageable,
			@RequestParam(required = false) String search) {
		log.info(">> getAllStaffDetails({}, {}, {}, {})", pageable, search);
		String email = AuthDetailsProvider.getLoggedEmail();
		return ResponseEntity.ok(profileService.getAllTeacher(email, Role.TEACHER, search, pageable));
	}

	@Operation(summary = "Get All Student Details API", description = "This API fetches All Student Details.")
	@GetMapping("/all-student")
	public ResponseEntity<PageResponse<Student>> getAllStudentDetails(Pageable pageable) {
		log.info(">> getAllStudentDetails({}, {})", pageable);
		String email = AuthDetailsProvider.getLoggedEmail();
		// profileService.getAllSchoolStudent(pageable);
		return ResponseEntity.ok(null);
	}

	@Operation(summary = "Create a new staff member", description = "Creates a new staff account using the StaffUserRequest request body for the currently logged-in admin user.")
	@PostMapping("/add-staff")
	public ResponseEntity<ApiResponse<String>> saveTeacherDetails(@Valid @RequestBody StaffUserRequest request) {
		log.info("API: Create NewUser with email {}", request.getEmail());
		String email = AuthDetailsProvider.getLoggedEmail();
		profileService.saveTeacherDetails(request, email);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.CREATED, "Staff detail successfully added!", null));
	}

	@Operation(summary = "Create a new driver", description = "Creates a driver login (DRIVER role) in the user table and the linked driver profile in the transport_staff table in a single call. Share the returned temporary password with the driver.")
	@PostMapping("/add-driver")
	public ResponseEntity<DriverResponse> createDriver(@Valid @RequestBody DriverRequest request) {
		log.info("API: Create driver {} with login {}", request.getName(), request.getEmail());
		return ResponseEntity.status(HttpStatus.CREATED).body(transportStaffService.createDriver(request));
	}

}
