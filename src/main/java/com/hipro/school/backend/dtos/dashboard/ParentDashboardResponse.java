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
@Schema(description = "Parent dashboard response")
public class ParentDashboardResponse {

	@Schema(description = "Parent profile information")
	private ParentProfileResponse profile;

	@Schema(description = "List of children")
	private List<ParentChildResponse> children;

	@Schema(description = "Selected child's detailed information (if studentId param provided)")
	private ParentChildDetailResponse selectedChildDetail;

	@Schema(description = "Recent notices applicable to parent")
	private List<RecentNoticeResponse> recentNotices;
}
