package com.hipro.school.backend.dtos.dashboard;

import java.math.BigDecimal;

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
@Schema(description = "Exam result for dashboard")
public class ExamResultResponse {

	@Schema(description = "Result ID")
	private Long resultId;

	@Schema(description = "Exam title")
	private String examTitle;

	@Schema(description = "Subject name")
	private String subjectName;

	@Schema(description = "Total marks")
	private BigDecimal totalMarks;

	@Schema(description = "Obtained marks")
	private BigDecimal obtainedMarks;

	@Schema(description = "Percentage")
	private BigDecimal percentage;

	@Schema(description = "Grade")
	private String grade;

	@Schema(description = "Whether passed")
	private Boolean passed;

	@Schema(description = "Remarks")
	private String remarks;
}
