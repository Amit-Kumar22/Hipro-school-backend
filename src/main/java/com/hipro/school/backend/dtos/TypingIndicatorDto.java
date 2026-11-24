package com.hipro.school.backend.dtos;

import lombok.Data;

@Data
public class TypingIndicatorDto {

	private Long conversationId;
	private boolean typing;
}
