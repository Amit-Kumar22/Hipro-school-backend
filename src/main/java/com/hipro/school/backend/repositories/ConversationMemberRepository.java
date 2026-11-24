package com.hipro.school.backend.repositories;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hipro.school.backend.entities.ConversationMember;

@Repository
public interface ConversationMemberRepository extends JpaRepository<ConversationMember, Long> {

	boolean existsByConversationIdAndUserEmail(Long conversationId, String email);

	List<ConversationMember> findByConversationId(Long conversationId);

	Optional<ConversationMember> findByConversationIdAndUserEmail(Long conversationId, String email);

	List<ConversationMember> findByUserId(Long userId);

	List<ConversationMember> findByUser_Email(String email);
}