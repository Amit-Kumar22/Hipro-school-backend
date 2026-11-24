package com.hipro.school.backend.dtos.dashboard;

import java.math.BigDecimal;
import java.time.LocalDate;
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
@Schema(description = "Student dashboard response")
public class StudentDashboardResponse {

	@Schema(description = "Student profile information")
	private StudentProfileResponse profile;

	@Schema(description = "Attendance summary")
	private StudentAttendanceSummaryResponse attendance;

	@Schema(description = "Fee summary")
	private StudentFeeSummaryResponse fees;

	@Schema(description = "Exam summary")
	private StudentExamSummaryResponse exams;

	@Schema(description = "Today's timetable")
	private List<TimetableEntryResponse> todayTimetable;

	@Schema(description = "Recent notices")
	private List<RecentNoticeResponse> recentNotices;

	@Schema(description = "Leave summary")
	private StudentLeaveSummaryResponse leave;

	@Schema(description = "Recent exam results")
	private List<ExamResultResponse> recentResults;
}
