package com.hipro.school.backend.dtos;

import lombok.Data;

@Data
public class SendMessageRequest {

	private Long chatRoomId;

	private String message;
}