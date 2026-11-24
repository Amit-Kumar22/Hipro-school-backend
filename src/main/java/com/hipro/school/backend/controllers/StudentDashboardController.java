package com.hipro.school.backend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hipro.school.backend.dtos.ApiResponse;
import com.hipro.school.backend.dtos.dashboard.StudentDashboardResponse;
import com.hipro.school.backend.security.AuthDetailsProvider;
import com.hipro.school.backend.services.StudentDashboardService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Student Dashboard APIs", description = "APIs for the student dashboard providing personal academic information")
@RestController
@RequestMapping("/v1/student/dashboard")
@RequiredArgsConstructor
@Slf4j
public class StudentDashboardController {

	private final StudentDashboardService studentDashboardService;

	@Operation(summary = "Get student dashboard", description = "Returns the authenticated student's profile, attendance, fees, exams, timetable, notices, and leave information.")
	@GetMapping
	public ResponseEntity<ApiResponse<StudentDashboardResponse>> getDashboard() {
		log.info(">> getDashboard()");
		String email = AuthDetailsProvider.getLoggedEmail();
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Student dashboard fetched successfully.",
				studentDashboardService.getDashboard(email)));
	}
}
