package com.hipro.school.backend.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hipro.school.backend.dtos.ConversationMemberResponse;
import com.hipro.school.backend.dtos.ConversationResponse;
import com.hipro.school.backend.dtos.CreateDirectConversationRequest;
import com.hipro.school.backend.dtos.CreateGroupConversationRequest;
import com.hipro.school.backend.entities.Conversation;
import com.hipro.school.backend.entities.ConversationMember;
import com.hipro.school.backend.entities.User;
import com.hipro.school.backend.enums.ConversationType;
import com.hipro.school.backend.exceptions.ResourceNotFoundException;
import com.hipro.school.backend.repositories.ConversationMemberRepository;
import com.hipro.school.backend.repositories.ConversationRepository;
import com.hipro.school.backend.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConversationService {

	private final ConversationRepository conversationRepository;
	private final ConversationMemberRepository memberRepository;
	private final UserRepository userRepository;

	@Transactional
	public ConversationResponse createDirectConversation(CreateDirectConversationRequest request, String email) {
		User currentUser = getUser(email);
		if (currentUser.getId().equals(request.getUserId())) {
			throw new IllegalArgumentException("You cannot create a conversation with yourself");
		}
		User otherUser = getUser(request.getUserId());
		Conversation conversation = new Conversation();
		conversation.setName(otherUser.getFullName());
		conversation.setType(ConversationType.DIRECT);
		conversation = conversationRepository.save(conversation);
		addMember(conversation, currentUser);
		addMember(conversation, otherUser);
		return mapToResponse(conversation, email);
	}

	@Transactional
	public ConversationResponse createGroupConversation(CreateGroupConversationRequest request, Long currentUserId) {
		List<Long> userIds = new ArrayList<>(request.getUserIds());
		if (!userIds.contains(currentUserId)) {
			userIds.add(currentUserId);
		}

		Conversation conversation = new Conversation();
		conversation.setName(request.getName());
		conversation.setType(ConversationType.GROUP);
		conversation = conversationRepository.save(conversation);
		for (Long userId : userIds) {
			User user = getUser(userId);
			addMember(conversation, user);
		}
		User currentUser = getUser(currentUserId);
		return mapToResponse(conversation, currentUser.getEmail());
	}

	@Transactional(readOnly = true)
	public ConversationResponse getConversation(Long conversationId, String email) {
		checkMembership(conversationId, email);
		Conversation conversation = conversationRepository.findById(conversationId)
				.orElseThrow(() -> new RuntimeException("Conversation not found"));
		return mapToResponse(conversation, email);
	}

	@Transactional(readOnly = true)
	public List<ConversationResponse> getMyConversations(String email) {
		return memberRepository.findByUser_Email(email).stream()
				.map(member -> mapToResponse(member.getConversation(), email)).toList();
	}

	private void addMember(Conversation conversation, User user) {
		ConversationMember member = new ConversationMember();
		member.setConversation(conversation);
		member.setUser(user);
		memberRepository.save(member);
	}

	public void checkMembership(Long conversationId, String email) {
		boolean exists = memberRepository.existsByConversationIdAndUserEmail(conversationId, email);
		if (!exists) {
			throw new AccessDeniedException("You are not a member of this conversation");
		}

	}

	private User getUser(Long userId) {
		return userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
	}

	private User getUser(String email) {
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
	}

	private ConversationResponse mapToResponse(Conversation conversation, String currentUserEmail) {
		List<ConversationMemberResponse> members = memberRepository.findByConversationId(conversation.getId()).stream()
				.map(member -> ConversationMemberResponse.builder().userId(member.getUser().getId())
						.fullName(member.getUser().getFullName()).email(member.getUser().getEmail())
						.role(member.getUser().getRole().name()).build())
				.toList();
		String name = conversation.getName();
		if (conversation.getType() == ConversationType.DIRECT) {
			name = members.stream().filter(m -> !m.getEmail().equals(currentUserEmail))
					.map(ConversationMemberResponse::getFullName).findFirst().orElse(conversation.getName());
		}

		return ConversationResponse.builder().id(conversation.getId()).type(conversation.getType()).name(name)
				.createdAt(conversation.getCreatedAt()).updatedAt(conversation.getUpdatedAt()).members(members).build();
	}
}