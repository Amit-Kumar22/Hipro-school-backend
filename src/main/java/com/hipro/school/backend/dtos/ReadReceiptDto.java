package com.hipro.school.backend.dtos;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReadReceiptDto {

	private Long conversationId;
	private Long userId;
	private String userName;
	private Long lastReadMessageId;
	private LocalDateTime readAt;
}
