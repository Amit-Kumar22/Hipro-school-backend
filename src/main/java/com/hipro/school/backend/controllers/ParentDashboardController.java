package com.hipro.school.backend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hipro.school.backend.dtos.ApiResponse;
import com.hipro.school.backend.dtos.dashboard.ParentDashboardResponse;
import com.hipro.school.backend.security.AuthDetailsProvider;
import com.hipro.school.backend.services.ParentDashboardService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Parent Dashboard APIs", description = "APIs for the parent dashboard providing children's academic information")
@RestController
@RequestMapping("/v1/parent/dashboard")
@RequiredArgsConstructor
@Slf4j
public class ParentDashboardController {

	private final ParentDashboardService parentDashboardService;

	@Operation(summary = "Get parent dashboard", description = "Returns the authenticated parent's profile, list of children, and selected child's detailed information including attendance, fees, exams, timetable, and leave.")
	@GetMapping
	public ResponseEntity<ApiResponse<ParentDashboardResponse>> getDashboard(
			@Parameter(description = "Selected child's student ID for detailed view") @RequestParam(required = false) Long studentId) {
		log.info(">> getDashboard(studentId={})", studentId);
		String email = AuthDetailsProvider.getLoggedEmail();
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Parent dashboard fetched successfully.",
				parentDashboardService.getDashboard(email, studentId)));
	}
}
