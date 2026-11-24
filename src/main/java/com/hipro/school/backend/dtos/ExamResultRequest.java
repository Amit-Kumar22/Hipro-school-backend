package com.hipro.school.backend.dtos;

import java.math.BigDecimal;

import com.hipro.school.backend.enums.ResultStatus;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
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
public class ExamResultRequest {

	@NotNull(message = "Exam ID is required")
	@Positive(message = "Exam ID must be greater than zero")
	private Long examId;

	@NotNull(message = "Exam subject ID is required")
	@Positive(message = "Exam subject ID must be greater than zero")
	private Long examSubjectId;

	@NotNull(message = "Student ID is required")
	private Long studentId;

	@DecimalMin(value = "0.0", inclusive = true, message = "Obtained marks cannot be negative")
	@Digits(integer = 6, fraction = 2, message = "Obtained marks must have at most 6 integer digits and 2 decimal places")
	private BigDecimal obtainedMarks;

	@NotNull(message = "Absent status is required")
	@Builder.Default
	private Boolean absent = false;

	@Size(max = 500, message = "Remarks cannot exceed 500 characters")
	private String remarks;

	@Size(max = 10, message = "Grade cannot exceed 10 characters")
	@NotBlank(message = "Grade is required")
	private String grade;

	private ResultStatus status;
}