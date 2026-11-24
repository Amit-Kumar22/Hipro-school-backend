package com.hipro.school.backend.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hipro.school.backend.entities.Question;
import com.hipro.school.backend.enums.QuestionType;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

	Page<Question> findAllBySubjectId(Long subjectId, Pageable pageable);

	Page<Question> findAllBySubjectIdAndType(Long subjectId, QuestionType type, Pageable pageable);

	List<Question> findAllBySubjectId(Long subjectId);

	
}