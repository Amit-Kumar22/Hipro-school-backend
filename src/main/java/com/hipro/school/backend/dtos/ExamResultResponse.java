package com.hipro.school.backend.dtos;

import java.math.BigDecimal;

import com.hipro.school.backend.enums.ResultStatus;

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
public class ExamResultResponse {

	private Long id;

	private Long examId;
	private String examTitle;

	private Long examSubjectId;
	private Long subjectId;
	private String subjectName;

	private Long studentId;
	private String studentName;

	
	private BigDecimal totalMarks;
	private BigDecimal passingMarks;
	private BigDecimal obtainedMarks;
	private BigDecimal percentage;

	private String grade;

	private Boolean absent;
	private Boolean passed;

	private String remarks;

	private ResultStatus status;
}