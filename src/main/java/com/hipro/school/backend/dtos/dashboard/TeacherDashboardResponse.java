package com.hipro.school.backend.dtos.dashboard;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Teacher dashboard response")
public class TeacherDashboardResponse {

	@Schema(description = "Teacher profile information")
	private TeacherProfileResponse profile;

	@Schema(description = "Assigned classes summary")
	private List<TeacherClassResponse> assignedClasses;

	@Schema(description = "Student overview")
	private TeacherStudentOverviewResponse studentOverview;

	@Schema(description = "Today's attendance for assigned classes")
	private TeacherAttendanceOverviewResponse attendanceOverview;

	@Schema(description = "Exam summary")
	private TeacherExamSummaryResponse examSummary;

	@Schema(description = "Today's timetable")
	private List<TimetableEntryResponse> todayTimetable;

	@Schema(description = "Recent notices")
	private List<RecentNoticeResponse> recentNotices;

	@Schema(description = "Homework summary")
	private TeacherHomeworkSummaryResponse homeworkSummary;
}
