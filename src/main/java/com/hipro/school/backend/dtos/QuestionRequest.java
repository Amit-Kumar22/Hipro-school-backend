package com.hipro.school.backend.dtos;

import java.math.BigDecimal;
import java.util.List;

import com.hipro.school.backend.enums.QuestionDifficulty;
import com.hipro.school.backend.enums.QuestionType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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
public class QuestionRequest {

	
	@NotNull(message = "Exam ID is required")
	private Long examId;

	@NotNull(message = "Question order is required")
	@Min(value = 1, message = "Question order must be greater than 0")
	private Integer questionOrder;

	@NotBlank(message = "Question text is required")
	private String questionText;

	@NotNull(message = "Question type is required")
	private QuestionType type;

	@NotNull(message = "Marks are required")
	@DecimalMin(value = "0.01", message = "Marks must be greater than 0")
	private BigDecimal marks;

	private QuestionDifficulty difficulty;

	@NotNull(message = "Subject ID is required")
	private Long subjectId;

	// Required for SHORT_ANSWER and LONG_ANSWER.
	private String modelAnswer;

	// Required for MCQ and TRUE_FALSE.
	private List<QuestionOptionRequest> options;
}