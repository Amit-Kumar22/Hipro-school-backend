package com.hipro.school.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.hipro.school.backend.entities.ExamQuestion;

@Repository
public interface ExamQuestionRepository
		extends JpaRepository<ExamQuestion, Long>, JpaSpecificationExecutor<ExamQuestion> {

	boolean existsByExamIdAndQuestionId(Long examId, Long questionId);

}
