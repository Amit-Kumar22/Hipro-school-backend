package com.hipro.school.backend.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hipro.school.backend.dtos.MessageResponse;
import com.hipro.school.backend.dtos.ReadReceiptDto;
import com.hipro.school.backend.dtos.SendMsgRequest;
import com.hipro.school.backend.dtos.TypingBroadcastDto;
import com.hipro.school.backend.entities.Conversation;
import com.hipro.school.backend.entities.ConversationMember;
import com.hipro.school.backend.entities.Message;
import com.hipro.school.backend.entities.User;
import com.hipro.school.backend.enums.MessageStatus;
import com.hipro.school.backend.enums.MessageType;
import com.hipro.school.backend.exceptions.ResourceNotFoundException;
import com.hipro.school.backend.repositories.ConversationMemberRepository;
import com.hipro.school.backend.repositories.ConversationRepository;
import com.hipro.school.backend.repositories.MessageRepository;
import com.hipro.school.backend.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessageService {

	private final MessageRepository messageRepository;
	private final ConversationMemberRepository memberRepository;
	private final ConversationRepository conversationRepository;
	private final ConversationService conversationService;
	private final UserRepository userRepository;
	private final SimpMessagingTemplate messagingTemplate;

	@Transactional
	public MessageResponse sendMessage(Long conversationId, SendMsgRequest request, String email) {
		conversationService.checkMembership(conversationId, email);

		Conversation conversation = conversationRepository.findById(conversationId)
				.orElseThrow(() -> new ResourceNotFoundException("Conversation not found. :: " + conversationId));
		User sender = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		Message message = new Message();
		message.setConversation(conversation);
		message.setSender(sender);
		message.setType(MessageType.TEXT);
		message.setContent(request.getContent());
		message.setStatus(MessageStatus.SENT);
		Message saved = messageRepository.save(message);
		MessageResponse response = mapToResponse(saved);
		messagingTemplate.convertAndSend("/topic/conversation/" + conversationId, response);
		return response;
	}

	@Transactional(readOnly = true)
	public List<MessageResponse> getMessages(Long conversationId, String email) {
		conversationService.checkMembership(conversationId, email);
		return messageRepository.findTop50ByConversationIdOrderByIdDesc(conversationId).stream()
				.map(this::mapToResponse).toList();
	}

	private MessageResponse mapToResponse(Message message) {
		return MessageResponse.builder().id(message.getId()).conversationId(message.getConversation().getId())
				.senderId(message.getSender().getId()).senderName(message.getSender().getFullName())
				.content(message.getContent()).status(message.getStatus())
				.createdAt(message.getCreatedAt()).build();
	}

	@Transactional
	public void markAsRead(Long conversationId, String email) {
		ConversationMember member = memberRepository.findByConversationIdAndUserEmail(conversationId, email)
				.orElseThrow(() -> new AccessDeniedException("You are not a member"));
		member.setLastReadAt(LocalDateTime.now());
		memberRepository.save(member);
	}

	@Transactional
	public void markAsReadAndBroadcast(Long conversationId, Long lastMessageId, String email) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		ConversationMember member = memberRepository.findByConversationIdAndUserEmail(conversationId, email)
				.orElseThrow(() -> new AccessDeniedException("You are not a member"));
		member.setLastReadAt(LocalDateTime.now());
		memberRepository.save(member);

		ReadReceiptDto receipt = ReadReceiptDto.builder()
				.conversationId(conversationId)
				.userId(user.getId())
				.userName(user.getFullName())
				.lastReadMessageId(lastMessageId)
				.readAt(LocalDateTime.now())
				.build();
		messagingTemplate.convertAndSend("/topic/conversation/" + conversationId + "/read", receipt);
	}

	@Transactional
	public void deleteMessage(Long messageId, String email) {
		Message message = messageRepository.findById(messageId)
				.orElseThrow(() -> new RuntimeException("Message not found"));
		if (!message.getSender().getEmail().equals(email)) {
			throw new AccessDeniedException("You can delete only your own messages");
		}
		message.setStatus(MessageStatus.DELETED);
		message.setDeletedAt(LocalDateTime.now());
		messageRepository.save(message);
		MessageResponse response = mapToResponse(message);
		messagingTemplate.convertAndSend("/topic/conversation/" + message.getConversation().getId(), response);
	}

	public void broadcastTyping(Long conversationId, Long userId, String userName, boolean typing) {
		TypingBroadcastDto dto = TypingBroadcastDto.builder()
				.conversationId(conversationId)
				.userId(userId)
				.userName(userName)
				.typing(typing)
				.build();
		messagingTemplate.convertAndSend("/topic/conversation/" + conversationId + "/typing", dto);
	}
}
