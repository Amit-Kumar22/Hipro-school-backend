package com.hipro.school.backend.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hipro.school.backend.dtos.ApiResponse;
import com.hipro.school.backend.dtos.dashboard.AttendanceOverviewResponse;
import com.hipro.school.backend.dtos.dashboard.ClassStatsResponse;
import com.hipro.school.backend.dtos.dashboard.FeeOverviewResponse;
import com.hipro.school.backend.dtos.dashboard.RecentNoticeResponse;
import com.hipro.school.backend.dtos.dashboard.RevenueTrendResponse;
import com.hipro.school.backend.dtos.dashboard.SummaryResponse;
import com.hipro.school.backend.services.AdminDashboardService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Admin Dashboard APIs", description = "APIs for the admin dashboard providing aggregated statistics and overview data")
@RestController
@RequestMapping("/v1/admin/dashboard")
@RequiredArgsConstructor
@Slf4j
public class AdminDashboardController {

	private final AdminDashboardService adminDashboardService;

	@Operation(summary = "Get dashboard summary", description = "Returns overall counts of students, teachers, classes, subjects, active users, notices, and exams.")
	@GetMapping("/summary")
	public ResponseEntity<ApiResponse<SummaryResponse>> getSummary() {
		log.info(">> getSummary()");
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Dashboard summary fetched successfully.",
				adminDashboardService.getSummary()));
	}

	@Operation(summary = "Get fee overview", description = "Returns fee collection statistics including total fees, collected, pending, overdue amounts and counts.")
	@GetMapping("/fee-overview")
	public ResponseEntity<ApiResponse<FeeOverviewResponse>> getFeeOverview() {
		log.info(">> getFeeOverview()");
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Fee overview fetched successfully.",
				adminDashboardService.getFeeOverview()));
	}

	@Operation(summary = "Get attendance overview", description = "Returns today's attendance breakdown with present, absent, half-day, late counts and percentage.")
	@GetMapping("/attendance-overview")
	public ResponseEntity<ApiResponse<AttendanceOverviewResponse>> getAttendanceOverview() {
		log.info(">> getAttendanceOverview()");
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Attendance overview fetched successfully.",
				adminDashboardService.getAttendanceOverview()));
	}

	@Operation(summary = "Get recent notices", description = "Returns a list of recently published notices.")
	@GetMapping("/recent-notices")
	public ResponseEntity<ApiResponse<List<RecentNoticeResponse>>> getRecentNotices(
			@RequestParam(defaultValue = "5") int limit) {
		log.info(">> getRecentNotices(limit={})", limit);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Recent notices fetched successfully.",
				adminDashboardService.getRecentNotices(limit)));
	}

	@Operation(summary = "Get class-wise statistics", description = "Returns student count, teacher count, and fee structure count for each class.")
	@GetMapping("/class-stats")
	public ResponseEntity<ApiResponse<List<ClassStatsResponse>>> getClassStats() {
		log.info(">> getClassStats()");
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Class statistics fetched successfully.",
				adminDashboardService.getClassStats()));
	}

	@Operation(summary = "Get revenue trend", description = "Returns monthly revenue data for the specified number of months.")
	@GetMapping("/revenue-trend")
	public ResponseEntity<ApiResponse<List<RevenueTrendResponse>>> getRevenueTrend(
			@RequestParam(defaultValue = "6") int months) {
		log.info(">> getRevenueTrend(months={})", months);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Revenue trend fetched successfully.",
				adminDashboardService.getRevenueTrend(months)));
	}
}
