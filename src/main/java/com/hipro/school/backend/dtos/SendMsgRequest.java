package com.hipro.school.backend.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SendMsgRequest {

	@NotNull
	private Long conversationId;

	@Size(max = 10000)
	@NotBlank(message = "Message content cannot be blank")
	private String content;
}
