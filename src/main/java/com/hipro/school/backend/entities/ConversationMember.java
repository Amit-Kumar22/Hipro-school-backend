package com.hipro.school.backend.entities;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "conversation_members", uniqueConstraints = {
		@UniqueConstraint(name = "uk_conversation_user", columnNames = { "conversation_id", "user_id" }) }, indexes = {
				@Index(name = "idx_conversation_member_user", columnList = "user_id"),
				@Index(name = "idx_conversation_member_conversation", columnList = "conversation_id") })
public class ConversationMember {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "conversation_id", nullable = false)
	private Conversation conversation;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(nullable = false, updatable = false)
	private LocalDateTime joinedAt;

	private LocalDateTime lastReadAt;

	@PrePersist
	protected void onCreate() {
		joinedAt = LocalDateTime.now();
	}
}