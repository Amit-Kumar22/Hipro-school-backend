package com.hipro.school.backend.controllers;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hipro.school.backend.dtos.AccessPermissionResponse;
import com.hipro.school.backend.dtos.ApiResponse;
import com.hipro.school.backend.dtos.HomeworkResponse;
import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.dtos.PermissionPageResponse;
import com.hipro.school.backend.dtos.StaffAccessControlResponse;
import com.hipro.school.backend.dtos.TeacherResponse;
import com.hipro.school.backend.dtos.UserResponse;
import com.hipro.school.backend.dtos.WeeklyTimetableResponse;
import com.hipro.school.backend.enums.AccessAttributeName;
import com.hipro.school.backend.security.AuthDetailsProvider;
import com.hipro.school.backend.services.AttendanceService;
import com.hipro.school.backend.services.HomeworkService;
import com.hipro.school.backend.services.StaffService;
import com.hipro.school.backend.services.TeacherDashboardService;
import com.hipro.school.backend.services.WeeklyTimetableService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "Staff User APIs", description = "This collection contains APIs for Staff resources")
@RestController
@RequestMapping("/v1/teacher")
public class TeacherController {

	@Autowired
	private AttendanceService attendanceService;

	@Autowired
	private HomeworkService homeworkService;

	@Autowired
	private WeeklyTimetableService timetableService;

	@Autowired
	private StaffService staffService;

	@Autowired
	private TeacherDashboardService teacherDashboardService;

	@Operation(summary = "Get all Staff Detail Permissions for teacher")
	@GetMapping("/role-access")
	public ResponseEntity<List<StaffAccessControlResponse>> getAllStaffPermissions() {
		log.info(">> getAllStaff({})");
		String email = AuthDetailsProvider.getLoggedEmail();
		return ResponseEntity.ok(staffService.getUserStaffPermission(email));
	}

	@Operation(summary = "Get all teachers by subject id")
	@GetMapping("/by-subject")
	public ResponseEntity<List<TeacherResponse>> getAllTeacherBySubjectId(@RequestParam Long subjectId) {
		log.info(">> getAllTeacherBySubjectId({})", subjectId);
		return ResponseEntity.ok(teacherDashboardService.getAllTeacherBySubjectId(subjectId));
	}

	@Operation(summary = "Get students list for class teacher with attendance access permission", description = "Returns paginated students of the teacher's class along with the logged-in teacher's ATTENDANCE permission (canRead / canAdd / canUpdate / canDelete) so the UI can toggle icons such as check-in.")
	@GetMapping("/attendance/all-student-by-class-teacher")
	public ResponseEntity<PermissionPageResponse<UserResponse>> getAllStudentByClassTeacher(
			@RequestParam(required = false) String reqEmail, Pageable pageable) {
		String email = (reqEmail != null && !reqEmail.isBlank()) ? reqEmail : AuthDetailsProvider.getLoggedEmail();
		PageResponse<UserResponse> students = attendanceService.getAllStudentByClassTeacher(email, pageable);
		AccessPermissionResponse accessPermission = staffService.getAccessPermission(email,
				AccessAttributeName.ATTENDANCE);
		return ResponseEntity.ok(PermissionPageResponse.from(students, accessPermission));
	}

	@Operation(summary = "Get teacher timetable with filters", description = "Fetches timetable for the logged-in teacher. Supports filtering by classId, subjectId, and dayOfWeek.")
	@GetMapping("/time-table")
	public ResponseEntity<ApiResponse<List<WeeklyTimetableResponse>>> getTeacherTimetable(
			@RequestParam(required = false) Long classId, @RequestParam(required = false) Long subjectId,
			@RequestParam(required = false) DayOfWeek dayOfWeek) {
		String email = AuthDetailsProvider.getLoggedEmail();
		log.info(">> get teacher timetable: email={}, classId={}, subjectId={}, dayOfWeek={}", email, classId,
				subjectId, dayOfWeek);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Teacher timetable fetched successfully.",
				timetableService.getTeacherTimetable(email, classId, subjectId, dayOfWeek)));
	}

	@Operation(summary = "Get homework by filters", description = "Fetches homework filtered by teacher, section, subject, and date range.")
	@GetMapping("/home-work/teacher")
	public ResponseEntity<PageResponse<HomeworkResponse>> getAllHomeWork(@RequestParam(required = false) Long teacherId,
			@RequestParam(required = false) Long sectionId, @RequestParam(required = false) Long subjectId,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
			Pageable pageable) {
		String email = AuthDetailsProvider.getLoggedEmail();
		return ResponseEntity
				.ok(homeworkService.getAllHomeWork(teacherId, sectionId, subjectId, fromDate, toDate, email, pageable));
	}

}
