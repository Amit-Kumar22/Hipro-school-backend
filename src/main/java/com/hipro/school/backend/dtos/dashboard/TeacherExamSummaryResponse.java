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
@Schema(description = "Teacher's exam summary for dashboard")
public class TeacherExamSummaryResponse {

	@Schema(description = "Total upcoming exams for assigned classes")
	private long upcomingExams;

	@Schema(description = "Total completed exams for assigned classes")
	private long completedExams;

	@Schema(description = "Number of pending result entries")
	private long pendingResults;
}
