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
@Schema(description = "Teacher's homework summary for dashboard")
public class TeacherHomeworkSummaryResponse {

	@Schema(description = "Total homework assigned by this teacher")
	private long totalHomework;

	@Schema(description = "Homework assigned this week")
	private long thisWeekHomework;
}
