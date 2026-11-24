package com.hipro.school.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hipro.school.backend.entities.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {

	//Optional<Message> findByClientMessageId(String clientMessageId);

	List<Message> findTop50ByConversationIdOrderByIdDesc(Long conversationId);
}