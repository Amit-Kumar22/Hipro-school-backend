package com.hipro.school.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.hipro.school.backend.entities.Chatbot;

@Repository
public interface ChatbotRepository extends JpaRepository<Chatbot, Long> {

	@Query("SELECT c FROM Chatbot c WHERE (LOWER(c.keyword) LIKE LOWER(CONCAT('%', :keyword, '%')) "
			+ "OR LOWER(c.answer) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND c.active = true")
	List<Chatbot> findByKeywordAndActiveTrue(String keyword);

	boolean existsByKeyword(String keyword);
}