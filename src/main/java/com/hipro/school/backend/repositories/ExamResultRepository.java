package com.hipro.school.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hipro.school.backend.entities.ExamResult;
import com.hipro.school.backend.enums.ResultStatus;

@Repository
public interface ExamResultRepository extends JpaRepository<ExamResult, Long>, JpaSpecificationExecutor<ExamResult> {

	boolean existsByExamIdAndExamSubjectIdAndStudentId(Long examId, Long examSubjectId, Long studentId);

	List<ExamResult> findByStudent_IdOrderByCreatedDesc(Long studentId);

	@Query("SELECT COUNT(er) FROM ExamResult er WHERE er.student.id = :studentId AND er.passed = true")
	long countPassedByStudent(@Param("studentId") Long studentId);

	@Query("SELECT COUNT(er) FROM ExamResult er WHERE er.student.id = :studentId")
	long countByStudent(@Param("studentId") Long studentId);

	@Query("SELECT COUNT(er) FROM ExamResult er WHERE er.exam.id = :examId AND er.status = :status")
	long countByExamAndStatus(@Param("examId") Long examId, @Param("status") ResultStatus status);

	@Query("""
			SELECT er FROM ExamResult er
			JOIN FETCH er.student s
			JOIN FETCH s.studentUser su
			JOIN FETCH s.schoolClass sc
			JOIN FETCH er.exam e
			JOIN FETCH e.schoolClass ec
			JOIN FETCH er.examSubject es
			JOIN FETCH es.subject sub
			ORDER BY su.fullName ASC, e.startDate ASC, es.examDate ASC""")
	List<ExamResult> findAllForPdf();

	@Query("""
			SELECT er FROM ExamResult er
			JOIN FETCH er.student s
			JOIN FETCH s.studentUser su
			JOIN FETCH s.schoolClass sc
			JOIN FETCH er.exam e
			JOIN FETCH e.schoolClass ec
			JOIN FETCH er.examSubject es
			JOIN FETCH es.subject sub
			WHERE s.id = :studentId
			ORDER BY e.startDate ASC, es.examDate ASC""")
	List<ExamResult> findForPdfByStudentId(@Param("studentId") Long studentId);
}