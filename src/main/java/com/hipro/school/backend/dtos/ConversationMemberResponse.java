package com.hipro.school.backend.dtos;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ConversationMemberResponse {

	private Long userId;

	private String fullName;

	private String email;

	private String role;
}