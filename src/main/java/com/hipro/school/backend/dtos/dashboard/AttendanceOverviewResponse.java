package com.hipro.school.backend.dtos.dashboard;

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
@Schema(description = "Today's attendance overview for the dashboard")
public class AttendanceOverviewResponse {

	@Schema(description = "Date of attendance")
	private String date;

	@Schema(description = "Number of students present today")
	private long presentCount;

	@Schema(description = "Number of students absent today")
	private long absentCount;

	@Schema(description = "Number of students with half day")
	private long halfDayCount;

	@Schema(description = "Number of students late today")
	private long lateCount;

	@Schema(description = "Total attendance records for today")
	private long totalRecords;

	@Schema(description = "Attendance percentage (present / total * 100)")
	private double attendancePercentage;
}
