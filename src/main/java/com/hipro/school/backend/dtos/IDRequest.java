package com.hipro.school.backend.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class IDRequest {
	
	@NotNull(message = "Product id is required")
	@Positive(message = "Product id must be greater than 0")
	private Long id;

	@NotNull(message = "Active status is required")
	private Boolean isActive;
}
