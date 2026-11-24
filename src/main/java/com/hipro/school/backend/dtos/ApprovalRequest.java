package com.hipro.school.backend.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ApprovalRequest {

	@NotNull(message = "id are required.")
	private Integer id;

	private String message;

}
