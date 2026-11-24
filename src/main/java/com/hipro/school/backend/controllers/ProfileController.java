package com.hipro.school.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hipro.school.backend.dtos.ApiResponse;
import com.hipro.school.backend.dtos.ResetPasswordRequest;
import com.hipro.school.backend.dtos.UserRequest;
import com.hipro.school.backend.entities.User;
import com.hipro.school.backend.security.AuthDetailsProvider;
import com.hipro.school.backend.services.AuthenticationService;
import com.hipro.school.backend.services.ProfileService;
import com.hipro.school.backend.services.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/profile")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Profile Management", description = "operations for Profile")
public class ProfileController {

	@Autowired
	private ProfileService profileService;

	@Autowired
	private UserService userService;

	@Autowired
	private AuthenticationService authenticationService;

	@Operation(summary = "Get the logged-in user's profile", description = "Fetches the profile details of the currently authenticated user based on their email.")
	@GetMapping
	public ResponseEntity<ApiResponse<User>> getProfile() {
		log.info("API: Get Profle");
		String email = AuthDetailsProvider.getLoggedEmail();
		User res = profileService.getProfile(email);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Profile fetched successfully", res));
	}

	@Operation(summary = "Update profile", description = "Updates the profile of the currently authenticated user using the UserRequest request body.")
	@PutMapping("/update")
	public ResponseEntity<ApiResponse<Void>> updateProfile(@RequestBody UserRequest request) {
		log.info("API: Update Profle({})", request);
		String email = AuthDetailsProvider.getLoggedEmail();
		userService.updateUser(email, request);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@Operation(summary = "Update password", description = "Updates the password of the currently authenticated user using the PasswordRequest request body.")
	@PutMapping("/update-password")
	public ResponseEntity<ApiResponse<Void>> updatePassword(@Valid @RequestBody ResetPasswordRequest request) {
		log.info("API: Update Password for {}", request);
		String email = AuthDetailsProvider.getLoggedEmail();
		authenticationService.updatePassword(email, request);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

}
