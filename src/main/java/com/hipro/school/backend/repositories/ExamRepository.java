package com.hipro.school.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hipro.school.backend.entities.Exam;
import com.hipro.school.backend.enums.ExamStatus;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long>, JpaSpecificationExecutor<Exam> {

	@Query("SELECT COUNT(e) FROM Exam e WHERE e.status = :status")
	long countByStatus(@Param("status") ExamStatus status);

	@Query("SELECT e FROM Exam e WHERE e.schoolClass.id = :classId AND e.status = :status ORDER BY e.startDate ASC")
	List<Exam> findBySchoolClassAndStatus(@Param("classId") Long classId, @Param("status") ExamStatus status);
}