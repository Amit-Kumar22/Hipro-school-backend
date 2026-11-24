package com.hipro.school.backend.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.hipro.school.backend.enums.ExamStatus;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class TestExamRequest {

	@NotBlank(message = "Title name is required")
	@Size(max = 150)
	private String titleName;

	@NotNull(message = "Class ID is required")
	private Long schoolClassId;

	@NotNull(message = "Subject ID is required")
	private Long subjectId;

	@NotNull(message = "Duration is required")
	@Min(value = 1, message = "Duration must be greater than 0")
	private Integer durationMinutes;

	@NotNull(message = "Total marks is required")
	@DecimalMin(value = "0.01")
	private BigDecimal totalMarks;

	@NotNull(message = "Passing marks is required")
	@DecimalMin(value = "0.01")
	private BigDecimal passingMarks;

	private LocalDateTime startDate;

	@NotNull(message = "Status is required")
	private ExamStatus status;

	@Size(max = 500)
	private String description;
}