package com.hipro.school.backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hipro.school.backend.enums.ChatRoomType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "chat_room", indexes = { @Index(name = "idx_room_type", columnList = "type") })
@Getter
@Setter
public class ChatRoom extends BaseEntity {

	private String name; // Group name (null for private chat)

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "school_id")
	@JsonIgnore
	private School school;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "admin_id", nullable = false)
	private User admin;

	@Enumerated(EnumType.STRING)
	private ChatRoomType type; // PRIVATE / GROUP

}