package com.hipro.school.backend.dtos;

import java.util.List;

import com.hipro.school.backend.entities.ChatRoom;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupMessageResponse {
	private ChatRoom chatRoom;
	private List<ChatMessageResponse> chatMessages;
}
