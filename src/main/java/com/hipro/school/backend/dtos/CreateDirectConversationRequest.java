package com.hipro.school.backend.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateDirectConversationRequest {

	@NotNull
	private Long userId;
}