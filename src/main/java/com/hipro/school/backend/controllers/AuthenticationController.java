package com.hipro.school.backend.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hipro.school.backend.dtos.ApiResponse;
import com.hipro.school.backend.dtos.AuthenticationRequest;
import com.hipro.school.backend.dtos.ForgotPasswordRequest;
import com.hipro.school.backend.dtos.PasswordRequest;
import com.hipro.school.backend.dtos.RegisterRequest;
import com.hipro.school.backend.dtos.UserAuthenticationResponse;
import com.hipro.school.backend.dtos.VerificationRequest;
import com.hipro.school.backend.dtos.WebsiteContentResponse;
import com.hipro.school.backend.entities.WebsiteColorTheme;
import com.hipro.school.backend.security.AuthDetailsProvider;
import com.hipro.school.backend.services.AuthenticationService;
import com.hipro.school.backend.services.WebsiteColorThemeService;
import com.hipro.school.backend.services.WebsiteContentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "Authentication APIs", description = "APIs for authentication, registration, password and verification flow")
@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

	@Autowired
	private AuthenticationService service;

	@Autowired
	private WebsiteContentService websiteContentService;

	@Autowired
	private WebsiteColorThemeService websiteColorThemeService;

	@Operation(summary = "Registers a new school admin and returns verification status", description = "Registers a new school admin and returns the verification status.")
	@PostMapping("/register")
	public ResponseEntity<ApiResponse<Void>> schoolAdminRegister(@Valid @RequestBody RegisterRequest request) {
		log.info(">> register({})", request.getEmail());
		return ResponseEntity.ok(service.schoolAdminRegister(request));
	}

	@Operation(summary = "Authenticates a user and returns an access token", description = "Authenticates a user and returns an access token.")
	@PostMapping("/login")
	public ResponseEntity<UserAuthenticationResponse> authenticate(@Valid @RequestBody AuthenticationRequest request) {
		log.info(">> authenticate({})", request.getEmail());
		return ResponseEntity.ok(service.authenticate(request));
	}

	@Operation(summary = "Logs out the current user", description = "Logs out the current user.")
	@DeleteMapping("/logout")
	public ResponseEntity<ApiResponse<String>> logout() {
		log.info(">> logout()");
		String email = AuthDetailsProvider.getLoggedEmail();
		String message = service.logout(email);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, message, null));
	}

	@Operation(summary = "Sends a password reset link", description = "Sends a password reset link to the requesting user.")
	@PostMapping("/forgotPassword")
	public ResponseEntity<Void> forgotPassword(@RequestBody ForgotPasswordRequest forgetPassword) {
		log.info(">> forgetPassword({})", forgetPassword);
		service.forgetPassword(forgetPassword);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@Operation(summary = "Sets a new password", description = "Sets a new password for the user.")
	@PostMapping("/set-password")
	public ResponseEntity<Map<String, Object>> setPassword(@RequestBody PasswordRequest passwordRequest) {
		log.info(">> setPassword for code: {}", passwordRequest.getCode());
		return ResponseEntity.ok(service.setPassword(passwordRequest));
	}

	@Operation(summary = "Verifies a user account", description = "Verifies a user account.")
	@PutMapping("/verification")
	public ResponseEntity<Map<String, Object>> verificationUser(@RequestBody VerificationRequest request) {
		log.info(">> verificationUser({})", request.getEmail());
		return ResponseEntity.ok(service.verificationUser(request));
	}

	@GetMapping("/website-content")
	@Operation(summary = "Get school website content", description = "Returns the complete website content for the given school code used to render the public school website.")
	public ResponseEntity<ApiResponse<WebsiteContentResponse>> getWebsiteContent() {
		log.info(">> getWebsiteContent({})");
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Website content fetched successfully",
				websiteContentService.getWebsiteContent()));
	}

	@GetMapping("/active-color-theme")
	@Operation(summary = "Get active color theme", description = "Returns the currently active website color theme.")
	public ResponseEntity<ApiResponse<WebsiteColorTheme>> getActiveThemes() {
		log.info(">> getActiveThemes()");
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Active theme fetched successfully",
				websiteColorThemeService.getActiveThemes()));
	}

}
