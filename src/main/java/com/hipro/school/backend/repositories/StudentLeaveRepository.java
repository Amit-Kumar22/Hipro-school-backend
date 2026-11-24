package com.hipro.school.backend.repositories;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hipro.school.backend.entities.StudentLeave;
import com.hipro.school.backend.enums.LeaveStatus;

@Repository
public interface StudentLeaveRepository
		extends JpaRepository<StudentLeave, Long>, JpaSpecificationExecutor<StudentLeave> {

	@Query("""
			    SELECT sl FROM StudentLeave sl
			    WHERE sl.student.id = :studentId
			    AND sl.status IN :statuses
			    AND sl.fromDate <= :toDate
			    AND sl.toDate >= :fromDate
			""")
	List<StudentLeave> findOverlappingLeaves(@Param("studentId") Long studentId,
			@Param("statuses") Collection<LeaveStatus> statuses, @Param("fromDate") LocalDate fromDate,
			@Param("toDate") LocalDate toDate);

	@Query("SELECT COUNT(sl) FROM StudentLeave sl WHERE sl.student.id = :studentId AND sl.status = :status")
	long countByStudentAndStatus(@Param("studentId") Long studentId, @Param("status") LeaveStatus status);

	List<StudentLeave> findByStudent_IdOrderByFromDateDesc(Long studentId);

	@Query("SELECT COALESCE(SUM(sl.numberOfDays), 0) FROM StudentLeave sl WHERE sl.student.id = :studentId AND sl.status = :status")
	int sumDaysByStudentAndStatus(@Param("studentId") Long studentId, @Param("status") LeaveStatus status);
}
