package com.hipro.school.backend.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class IDListRequest {
	
	@NotNull(message = "ID is required")
	@Positive(message = "ID must be greater than 0")
	private Long id;

}
