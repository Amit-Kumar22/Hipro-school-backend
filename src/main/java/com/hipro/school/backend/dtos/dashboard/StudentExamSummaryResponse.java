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
@Schema(description = "Student exam summary for dashboard")
public class StudentExamSummaryResponse {

	@Schema(description = "Total exams taken")
	private long totalExams;

	@Schema(description = "Number of passed exams")
	private long passedExams;

	@Schema(description = "Number of failed exams")
	private long failedExams;

	@Schema(description = "Number of upcoming exams")
	private long upcomingExams;

	@Schema(description = "Average percentage")
	private double averagePercentage;

	@Schema(description = "Upcoming exams")
	private List<ExamBriefResponse> upcomingExamList;
}
