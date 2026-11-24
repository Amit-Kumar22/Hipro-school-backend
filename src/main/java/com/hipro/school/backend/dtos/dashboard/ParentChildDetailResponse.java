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
@Schema(description = "Parent's selected child detailed information")
public class ParentChildDetailResponse {

	@Schema(description = "Student profile")
	private StudentProfileResponse profile;

	@Schema(description = "Attendance summary")
	private StudentAttendanceSummaryResponse attendance;

	@Schema(description = "Fee summary")
	private StudentFeeSummaryResponse fees;

	@Schema(description = "Exam summary")
	private StudentExamSummaryResponse exams;

	@Schema(description = "Today's timetable")
	private List<TimetableEntryResponse> todayTimetable;

	@Schema(description = "Leave summary")
	private StudentLeaveSummaryResponse leave;

	@Schema(description = "Recent exam results")
	private List<ExamResultResponse> recentResults;
}
