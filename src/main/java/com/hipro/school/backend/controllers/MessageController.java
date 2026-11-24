package com.hipro.school.backend.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hipro.school.backend.dtos.MessageResponse;
import com.hipro.school.backend.dtos.SendMsgRequest;
import com.hipro.school.backend.security.AuthDetailsProvider;
import com.hipro.school.backend.services.MessageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/conversations")
@RequiredArgsConstructor
@Tag(name = "Message", description = "Message management APIs")
public class MessageController {

	private final MessageService messageService;

	@GetMapping("/{conversationId}/messages")
	@Operation(summary = "Get messages for a conversation", description = "Fetches the latest 50 messages for a conversation")
	public ResponseEntity<List<MessageResponse>> getMessages(@PathVariable Long conversationId) {
		String email = AuthDetailsProvider.getLoggedEmail();
		return ResponseEntity.ok(messageService.getMessages(conversationId, email));
	}

	@PutMapping("/{conversationId}/read")
	@Operation(summary = "Mark conversation as read", description = "Marks all messages in a conversation as read for the current user")
	public ResponseEntity<Void> markAsRead(@PathVariable Long conversationId) {
		String email = AuthDetailsProvider.getLoggedEmail();
		messageService.markAsRead(conversationId, email);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{conversationId}/messages/{messageId}")
	@Operation(summary = "Delete a message", description = "Soft-deletes a message. Only the sender can delete their own messages.")
	public ResponseEntity<Void> deleteMessage(@PathVariable Long conversationId, @PathVariable Long messageId) {
		String email = AuthDetailsProvider.getLoggedEmail();
		messageService.deleteMessage(messageId, email);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/send-message")
	@Operation(summary = "Send a message via HTTP", description = "Sends a message to a conversation. Prefer WebSocket /app/chat.send for real-time.")
	public void sendMessage(@RequestBody SendMsgRequest request) {
		String email = AuthDetailsProvider.getLoggedEmail();
		messageService.sendMessage(request.getConversationId(), request, email);
	}
}
