package com.hipro.school.backend.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "chat_room_member", uniqueConstraints = {
		@UniqueConstraint(name = "uk_room_user", columnNames = { "chat_room_id", "user_id" }) }, indexes = {
				@Index(name = "idx_user_room", columnList = "user_id, chat_room_id") })
@Getter
@Setter
public class ChatRoomMember extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "chat_room_id", nullable = false)
	private ChatRoom chatRoom;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	private Boolean isAdmin = false;

}
