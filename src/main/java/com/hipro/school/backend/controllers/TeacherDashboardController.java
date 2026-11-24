package com.hipro.school.backend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hipro.school.backend.dtos.ApiResponse;
import com.hipro.school.backend.dtos.dashboard.TeacherDashboardResponse;
import com.hipro.school.backend.security.AuthDetailsProvider;
import com.hipro.school.backend.services.TeacherDashboardService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Teacher Dashboard APIs", description = "APIs for the teacher dashboard providing class and student information")
@RestController
@RequestMapping("/v1/teacher/dashboard")
@RequiredArgsConstructor
@Slf4j
public class TeacherDashboardController {

	private final TeacherDashboardService teacherDashboardService;

	@Operation(summary = "Get teacher dashboard", description = "Returns the authenticated teacher's profile, assigned classes, student overview, attendance, exams, timetable, homework, and notices.")
	@GetMapping
	public ResponseEntity<ApiResponse<TeacherDashboardResponse>> getDashboard() {
		log.info(">> getDashboard()");
		String email = AuthDetailsProvider.getLoggedEmail();
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Teacher dashboard fetched successfully.",
				teacherDashboardService.getDashboard(email)));
	}
}
