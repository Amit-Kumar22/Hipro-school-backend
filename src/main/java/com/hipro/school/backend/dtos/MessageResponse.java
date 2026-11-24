package com.hipro.school.backend.dtos;

import java.time.LocalDateTime;

import com.hipro.school.backend.enums.MessageStatus;
import com.hipro.school.backend.enums.MessageType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MessageResponse {

	private Long id;

	private Long conversationId;

	private Long senderId;

	private String senderName;

	private String content;

	private MessageStatus status;

	private LocalDateTime createdAt;
}