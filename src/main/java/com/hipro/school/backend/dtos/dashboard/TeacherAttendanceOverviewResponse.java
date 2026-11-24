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
@Schema(description = "Teacher's attendance overview for assigned classes")
public class TeacherAttendanceOverviewResponse {

	@Schema(description = "Date")
	private String date;

	@Schema(description = "Total students present today across assigned classes")
	private long presentCount;

	@Schema(description = "Total students absent today across assigned classes")
	private long absentCount;

	@Schema(description = "Total students late today across assigned classes")
	private long lateCount;

	@Schema(description = "Total attendance records today")
	private long totalRecords;

	@Schema(description = "Attendance percentage")
	private double attendancePercentage;
}
