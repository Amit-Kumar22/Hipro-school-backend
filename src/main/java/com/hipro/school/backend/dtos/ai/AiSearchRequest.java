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
public class AiSearchRequest {

	@NotBlank
	@Size(max = 500)
	private String question;

	@Size(max = 50)
	private String provider;
}
