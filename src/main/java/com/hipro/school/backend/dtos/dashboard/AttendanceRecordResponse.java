package com.hipro.school.backend.dtos.dashboard;

import com.hipro.school.backend.enums.AttendanceStatus;

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
@Schema(description = "Individual attendance record for dashboard")
public class AttendanceRecordResponse {

	@Schema(description = "Attendance date")
	private String date;

	@Schema(description = "Attendance status")
	private AttendanceStatus status;

	@Schema(description = "Login time")
	private String loginTime;

	@Schema(description = "Logout time")
	private String logoutTime;

	@Schema(description = "Total working minutes")
	private Long totalWorkingMinutes;
}
