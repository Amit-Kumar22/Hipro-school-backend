package com.hipro.school.backend.dtos;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TypingBroadcastDto {

	private Long conversationId;
	private Long userId;
	private String userName;
	private boolean typing;
}
