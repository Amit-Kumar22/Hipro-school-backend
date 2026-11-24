package com.hipro.school.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hipro.school.backend.entities.ExamSection;

@Repository
public interface ExamSectionRepository extends JpaRepository<ExamSection, Long> {

	List<ExamSection> findAllByExamId(Long examId);

	void deleteAllByExamId(Long examId);

}