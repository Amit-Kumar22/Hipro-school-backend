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
@Schema(description = "Student attendance summary for dashboard")
public class StudentAttendanceSummaryResponse {

	@Schema(description = "Total attendance records")
	private long totalDays;

	@Schema(description = "Number of present days")
	private long presentDays;

	@Schema(description = "Number of absent days")
	private long absentDays;

	@Schema(description = "Number of late days")
	private long lateDays;

	@Schema(description = "Number of half days")
	private long halfDays;

	@Schema(description = "Attendance percentage")
	private double attendancePercentage;

	@Schema(description = "Recent attendance records")
	private List<AttendanceRecordResponse> recentAttendance;
}
