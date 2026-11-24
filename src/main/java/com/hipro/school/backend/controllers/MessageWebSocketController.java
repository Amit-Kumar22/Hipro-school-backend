package com.hipro.school.backend.controllers;

import java.security.Principal;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import com.hipro.school.backend.config.WebSocketJwtAuthInterceptor;
import com.hipro.school.backend.dtos.ReadReceiptRequest;
import com.hipro.school.backend.dtos.SendMsgRequest;
import com.hipro.school.backend.dtos.TypingIndicatorDto;
import com.hipro.school.backend.repositories.UserRepository;
import com.hipro.school.backend.services.MessageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MessageWebSocketController {

	private final MessageService messageService;
	private final UserRepository userRepository;

	@MessageMapping("/chat.send")
	public void sendMessage(SendMsgRequest request, Principal principal) {
		String email = principal.getName();
		messageService.sendMessage(request.getConversationId(), request, email);
	}

	@MessageMapping("/chat.typing/{conversationId}")
	public void typingIndicator(@DestinationVariable Long conversationId,
			@Payload TypingIndicatorDto request, Principal principal) {
		String email = principal.getName();
		userRepository.findByEmail(email).ifPresent(user -> {
			messageService.broadcastTyping(conversationId, user.getId(), user.getFullName(), request.isTyping());
		});
	}

	@MessageMapping("/chat.read")
	public void markAsRead(@Payload ReadReceiptRequest request, Principal principal) {
		String email = principal.getName();
		messageService.markAsReadAndBroadcast(request.getConversationId(), request.getLastMessageId(), email);
	}
}
