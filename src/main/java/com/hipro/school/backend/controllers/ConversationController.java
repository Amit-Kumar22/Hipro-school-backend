package com.hipro.school.backend.controllers;

import java.security.Principal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hipro.school.backend.dtos.ConversationResponse;
import com.hipro.school.backend.dtos.CreateDirectConversationRequest;
import com.hipro.school.backend.dtos.CreateGroupConversationRequest;
import com.hipro.school.backend.security.AuthDetailsProvider;
import com.hipro.school.backend.services.ConversationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/conversations")
@RequiredArgsConstructor
public class ConversationController {

	private final ConversationService conversationService;

	@PostMapping("/direct")
	public ResponseEntity<ConversationResponse> createDirect(
			@Valid @RequestBody CreateDirectConversationRequest request, Principal principal) {
		String email = AuthDetailsProvider.getLoggedEmail();
		return ResponseEntity.ok(conversationService.createDirectConversation(request, email));
	}

	@PostMapping("/group")
	public ResponseEntity<ConversationResponse> createGroup(@Valid @RequestBody CreateGroupConversationRequest request,
			Principal principal) {
		Long userId = Long.valueOf(principal.getName());
		return ResponseEntity.ok(conversationService.createGroupConversation(request, userId));
	}

	@GetMapping
	public ResponseEntity<List<ConversationResponse>> getMyConversations() {
		String email = AuthDetailsProvider.getLoggedEmail();
		return ResponseEntity.ok(conversationService.getMyConversations(email));
	}

	@GetMapping("/{conversationId}")
	public ResponseEntity<ConversationResponse> getConversation(@PathVariable Long conversationId) {
		String email = AuthDetailsProvider.getLoggedEmail();
		return ResponseEntity.ok(conversationService.getConversation(conversationId, email));
	}
}