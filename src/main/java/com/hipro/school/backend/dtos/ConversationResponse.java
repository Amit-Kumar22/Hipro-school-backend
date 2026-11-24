package com.hipro.school.backend.dtos;

import java.time.LocalDateTime;
import java.util.List;

import com.hipro.school.backend.enums.ConversationType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ConversationResponse {

	private Long id;
	
	private String name;

	private ConversationType type;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

	private List<ConversationMemberResponse> members;
}