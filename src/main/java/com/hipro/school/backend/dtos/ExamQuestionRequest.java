package com.hipro.school.backend.dtos;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
public class ExamQuestionRequest {

	@NotNull(message = "Exam ID is required")
	private Long examId;

	@NotNull(message = "Question ID is required")
	private Long questionId;

	@NotNull(message = "Question order is required")
	@Min(value = 1, message = "Question order must be greater than 0")
	private Integer questionOrder;

	@NotNull(message = "Marks is required")
	@DecimalMin(value = "0.01", message = "Marks must be greater than 0")
	private BigDecimal marks;
}
