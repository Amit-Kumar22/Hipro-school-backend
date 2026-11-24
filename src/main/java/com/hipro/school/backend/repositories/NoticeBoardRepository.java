package com.hipro.school.backend.repositories;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hipro.school.backend.entities.NoticeBoard;
import com.hipro.school.backend.enums.NoticeAudience;
import com.hipro.school.backend.enums.NoticeStatus;

@Repository
public interface NoticeBoardRepository extends JpaRepository<NoticeBoard, Long>, JpaSpecificationExecutor<NoticeBoard> {

	@Query("SELECT n FROM NoticeBoard n WHERE n.status = :status ORDER BY n.publishDate DESC")
	List<NoticeBoard> findTopByStatus(@Param("status") NoticeStatus status, Pageable pageable);

	@Query("SELECT n FROM NoticeBoard n WHERE n.audience IN :audiences AND n.status = :status ORDER BY n.publishDate DESC")
	List<NoticeBoard> findByAudienceInAndStatus(@Param("audiences") List<NoticeAudience> audiences, @Param("status") NoticeStatus status, Pageable pageable);
}