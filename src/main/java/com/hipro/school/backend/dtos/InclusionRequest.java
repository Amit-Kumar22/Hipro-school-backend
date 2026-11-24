package com.hipro.school.backend.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class InclusionRequest {

	@NotBlank(message = "Type is required")
	@Schema(description = "Type of inclusion", example = "Hotel")
	private String type;

	@Schema(description = "Category or sub-type", example = "3 Star")
	private String category;

	@Schema(description = "Description or custom detail", example = "Hotel Taj or Jet Airways")
	private String description;

}
