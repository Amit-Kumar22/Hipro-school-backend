package com.hipro.school.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hipro.school.backend.entities.ExamSubject;

@Repository
public interface ExamSubjectRepository extends JpaRepository<ExamSubject, Long> {

	List<ExamSubject> findAllByExam_IdOrderByExamDateAsc(Long examId);

	boolean existsByExam_IdAndSubject_Id(Long examId, Long subjectId);

	boolean existsByExam_IdAndSubject_IdAndIdNot(Long examId, Long subjectId, Long id);
}