package com.hipro.school.backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hipro.school.backend.dtos.AdminUserDto;
import com.hipro.school.backend.dtos.ApiResponse;
import com.hipro.school.backend.dtos.EmployeeListResponse;
import com.hipro.school.backend.dtos.UserRequest;
import com.hipro.school.backend.services.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Admin User Management APIs", description = "APIs for managing admin users and user settings")
@RestController
@RequestMapping("/v1/admin")
@Slf4j
public class SystemSettingController {

	@Autowired
	private UserService userService;

	@Operation(summary = "Get all users", description = "Fetches the list of all users. Supports an optional isDeleted query param to filter deleted users.")
	@GetMapping("/users")
	public ResponseEntity<ApiResponse<List<EmployeeListResponse>>> getAllUserList(
			@RequestParam(required = false) Boolean isDeleted) {
		log.info("Fetching all users with isDeleted: {}", isDeleted);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "All User fetched successfully",
				userService.getAllEmployee(isDeleted)));
	}

	@Operation(summary = "Get user details", description = "Fetches the details of a user by email path parameter.")
	@GetMapping("/user/{email}")
	public ResponseEntity<ApiResponse<EmployeeListResponse>> getUserDetailById(@PathVariable String email) {
		log.info("Fetching user detail by email: {}", email);
		return ResponseEntity
				.ok(ApiResponse.success(HttpStatus.OK, "User fetched successfully", userService.getByUserId(email)));
	}

	@Operation(summary = "Create a user", description = "Creates a new admin user using the AdminUserDto request body and delivers login credentials.")
	@PostMapping("/save-user")
	public ResponseEntity<ApiResponse<String>> updateUser(@RequestBody AdminUserDto user) {
		log.info("Saving new user with email: {}", user.getEmail());
		userService.adminSaveUser(user); // same method can be reused if ID is not null
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.NO_CONTENT,
				"The user has been created successfully, and login credentials have been sent via email.", null));
	}

	@Operation(summary = "Update a user", description = "Updates an existing user's details using the UserRequest request body.")
	@PostMapping("/update")
	public ResponseEntity<ApiResponse<String>> updateUser(@RequestBody UserRequest user) {
		log.info("Updating user: {}", user);
		userService.updateUser(null, user); // same method can be reused if ID is not null
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.NO_CONTENT, "user Details Updated successfully", null));
	}

	@Operation(summary = "Delete a user", description = "Deletes a user by email path parameter.")
	@DeleteMapping("/delete/{email}")
	public ResponseEntity<ApiResponse<String>> deleteLead(@PathVariable String email) {
		log.info("Deleting user by email: {}", email);
		userService.deleteUser(email);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.NO_CONTENT, "user Details deleted successfully", null));
	}

}
