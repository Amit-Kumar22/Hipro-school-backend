package com.hipro.school.backend.controllers;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hipro.school.backend.dtos.ApiResponse;
import com.hipro.school.backend.dtos.AttendanceVerifyResponse;
import com.hipro.school.backend.dtos.MarkAttendanceRequest;
import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.dtos.UserResponse;
import com.hipro.school.backend.entities.Attendance;
import com.hipro.school.backend.security.AuthDetailsProvider;
import com.hipro.school.backend.services.AttendanceService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/api/attendance")
@Slf4j
@Tag(name = "Attendance Management", description = "Manage daily attendance, breaks, and summaries")
public class AttendanceController {

	@Autowired
	private AttendanceService attendanceService;

	@PostMapping("/teacher-check-in")
	public ResponseEntity<AttendanceVerifyResponse> teacherCheckIn(
			@RequestBody MarkAttendanceRequest markAttendanceRequest) {
		log.info("API: checkIn for attendance {}", markAttendanceRequest);
		String email = AuthDetailsProvider.getLoggedEmail();
		return ResponseEntity.ok(attendanceService.teacherCheckIn(markAttendanceRequest, email));
	}

	@PostMapping("/student-check-in")
	public ResponseEntity<AttendanceVerifyResponse> studentCheckIn(@RequestParam Long studentUserId,
			@RequestBody MarkAttendanceRequest markAttendanceRequest) {
		log.info("API: checkIn for attendance {}", markAttendanceRequest);
		String email = AuthDetailsProvider.getLoggedEmail();
		return ResponseEntity.ok(attendanceService.studentCheckIn(studentUserId, markAttendanceRequest, email));
	}

	@PostMapping("/check-out")
	public ResponseEntity<AttendanceVerifyResponse> checkOut(@RequestBody MarkAttendanceRequest request) {
		log.info("API: check out for Attendance{}", request);
		String email = AuthDetailsProvider.getLoggedEmail();
		return ResponseEntity.ok(attendanceService.checkOut(request, email));
	}

	@GetMapping("/user-today")
	public ResponseEntity<ApiResponse<Attendance>> getTodayByEmployeeAttendence() {
		String email = AuthDetailsProvider.getLoggedEmail();
		Attendance attendance = attendanceService.getTodayByEmployeeAttendence(email);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Attendance fetched successfully", attendance));
	}

	@GetMapping("/user-today/user/{userId}")
	public ResponseEntity<ApiResponse<Attendance>> getTodayByStudentAttendence(@PathVariable Long userId) {
		Attendance attendance = attendanceService.getTodayByStudentAttendence(userId);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Attendance fetched successfully", attendance));
	}

	@GetMapping("/all-student-by-class-teacher")
	public ResponseEntity<PageResponse<UserResponse>> getAllStudentByClassTeacher(
			@RequestParam(required = false) String reqEmail, Pageable pageable) {
		String email = (reqEmail != null && !reqEmail.isBlank()) ? reqEmail : AuthDetailsProvider.getLoggedEmail();
		return ResponseEntity.ok(attendanceService.getAllStudentByClassTeacher(email, pageable));
	}

	@GetMapping("/user-wise-all")
	public ResponseEntity<PageResponse<Attendance>> getAllAttendenceForStaff(Pageable pageable,
			@RequestParam(required = false) LocalDate startDate, @RequestParam(required = false) LocalDate endDate) {
		String email = AuthDetailsProvider.getLoggedEmail();
		return ResponseEntity.ok(attendanceService.getAllAttendenceForStaff(pageable, email, startDate, endDate));
	}

	@GetMapping("/user-all/{userId}")
	public ResponseEntity<PageResponse<Attendance>> getAllAttendenceByUser(Pageable pageable, @PathVariable Long userId,
			@RequestParam(required = false) LocalDate startDate, @RequestParam(required = false) LocalDate endDate) {
		return ResponseEntity.ok(attendanceService.getAllAttendenceByUser(pageable, userId, startDate, endDate));
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<Attendance>> getAttendence(@PathVariable Long id) {
		return ResponseEntity
				.ok(ApiResponse.success(HttpStatus.OK, "Attendance fetched", attendanceService.getById(id)));
	}

	@GetMapping("/user-wise")
	public ResponseEntity<ApiResponse<List<Attendance>>> getByEmployeeAttendence() {
		String email = AuthDetailsProvider.getLoggedEmail();
		return ResponseEntity.ok(
				ApiResponse.success(HttpStatus.OK, "Attendance list fetched", attendanceService.getByEmployee(email)));
	}

	@GetMapping("/date/{date}")
	public ResponseEntity<ApiResponse<List<Attendance>>> getByDate(@PathVariable String date) {
		return ResponseEntity.ok(
				ApiResponse.success(HttpStatus.OK, "Attendance for date fetched", attendanceService.getByDate(date)));
	}

	@GetMapping("/summary")
	public ResponseEntity<ApiResponse<Map<String, Object>>> getSummary(@RequestParam int month,
			@RequestParam int year) {
		String email = AuthDetailsProvider.getLoggedEmail();
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Attendance summary fetched",
				attendanceService.getMonthlySummary(email, month, year)));
	}
}
