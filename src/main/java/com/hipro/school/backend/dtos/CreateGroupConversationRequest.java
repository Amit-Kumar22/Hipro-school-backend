package com.hipro.school.backend.dtos;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateGroupConversationRequest {

	private String name;

	@NotEmpty
	@Size(min = 2, max = 100)
	private List<Long> userIds;
}