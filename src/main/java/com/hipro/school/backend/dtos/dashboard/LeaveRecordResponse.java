package com.hipro.school.backend.dtos.dashboard;

import com.hipro.school.backend.enums.LeaveStatus;
import com.hipro.school.backend.enums.LeaveType;

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
@Schema(description = "Individual leave record for dashboard")
public class LeaveRecordResponse {

	@Schema(description = "Leave ID")
	private Long leaveId;

	@Schema(description = "Leave type")
	private LeaveType leaveType;

	@Schema(description = "Leave status")
	private LeaveStatus status;

	@Schema(description = "From date")
	private String fromDate;

	@Schema(description = "To date")
	private String toDate;

	@Schema(description = "Number of days")
	private Integer numberOfDays;

	@Schema(description = "Reason")
	private String reason;
}
