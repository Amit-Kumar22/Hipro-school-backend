package com.hipro.school.backend.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hipro.school.backend.entities.ChatRoom;
import com.hipro.school.backend.entities.School;

public interface SchoolRepository extends JpaRepository<School, Long> {

	Optional<School> findBySchoolCode(String schoolCode);

	Optional<School> findByUser_Id(Long userId);

	@Query("SELECT cr FROM ChatRoom cr WHERE cr.school.schoolCode = :schoolCode")
	Page<ChatRoom> findAllBySchoolCode(String schoolCode, Pageable pageable);

	@Query("""
			    SELECT crm.chatRoom FROM ChatRoomMember crm  WHERE crm.user.id = :userId

			""")
	Page<ChatRoom> findUserChatRooms(Long userId, Pageable pageable);

}