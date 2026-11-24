package com.hipro.school.backend.dtos;

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
public class QuestionOptionRequest {

	@NotBlank(message = "Option text is required")
	private String optionText;

	@NotNull(message = "Correct flag is required")
	private Boolean correct;

	@NotNull(message = "Option order is required")
	private Integer optionOrder;
}