package com.hipro.school.backend.dtos;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageResponse {
	private Long messageId;
	private String senderName;
	private String message;
	private LocalDateTime createdAt;
}
