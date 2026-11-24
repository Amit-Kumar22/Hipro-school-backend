package com.hipro.school.backend.dtos.ai;

import jakarta.validation.constraints.NotBlank;
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
public class AiQueryRequest {

	@NotBlank
	@Size(max = 500)
	private String question;

	// Optional AI provider override (e.g. openai, gemini, anthropic). Falls back to application.ai.default-provider.
	@Size(max = 50)
	private String provider;

	private Long classId;
}
