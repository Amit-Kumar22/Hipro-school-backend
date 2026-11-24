package com.hipro.school.backend.dtos;

import lombok.Data;

@Data
public class ReadReceiptRequest {

	private Long conversationId;
	private Long lastMessageId;
}
