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
@Schema(description = "Brief exam information for dashboard")
public class ExamBriefResponse {

	@Schema(description = "Exam ID")
	private Long examId;

	@Schema(description = "Exam title")
	private String title;

	@Schema(description = "Exam type")
	private String examType;

	@Schema(description = "Start date")
	private String startDate;

	@Schema(description = "End date")
	private String endDate;

	@Schema(description = "Exam status")
	private String status;

	@Schema(description = "Class name")
	private String className;
}
