package com.hipro.school.backend.repositories;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hipro.school.backend.entities.ChatRoomMember;

@Repository
public interface ChatRoomMemberRepo extends JpaRepository<ChatRoomMember, Long> {

	boolean existsByChatRoomIdAndUserId(Long roomId, Long userId);

	@Query("SELECT m.user.id FROM ChatRoomMember m WHERE m.chatRoom.id = :roomId AND m.user.id IN :userIds")
	List<Integer> findExistingUserIds(@Param("roomId") Long roomId, @Param("userIds") Set<Long> userIds);

}