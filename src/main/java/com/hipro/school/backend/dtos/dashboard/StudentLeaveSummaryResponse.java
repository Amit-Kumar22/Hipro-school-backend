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
@Schema(description = "Leave summary for dashboard")
public class StudentLeaveSummaryResponse {

	@Schema(description = "Total approved leave days")
	private int approvedDays;

	@Schema(description = "Total pending leave requests")
	private long pendingCount;

	@Schema(description = "Total rejected leave requests")
	private long rejectedCount;

	@Schema(description = "Recent leave applications")
	private java.util.List<LeaveRecordResponse> recentLeaves;
}
