package com.hipro.school.backend.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
public class ExamSubjectRequest {

	@NotNull
	private Long subjectId;

	@NotNull
	private LocalDateTime examDate;

	@NotNull
	@Positive
	private Integer durationMinutes;

	@NotNull
	@DecimalMin("0.01")
	@Digits(integer = 8, fraction = 2)
	private BigDecimal totalMarks;

	@NotNull
	@DecimalMin("0.01")
	@Digits(integer = 8, fraction = 2)
	private BigDecimal passingMarks;
}