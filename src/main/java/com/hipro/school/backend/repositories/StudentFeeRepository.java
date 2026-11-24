package com.hipro.school.backend.repositories;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hipro.school.backend.entities.StudentFee;
import com.hipro.school.backend.enums.FeeStatus;

@Repository
public interface StudentFeeRepository
		extends JpaRepository<StudentFee, Long>, JpaSpecificationExecutor<StudentFee> {

	boolean existsByStudent_IdAndFeeStructure_Id(Long studentId, Long feeStructureId);

	List<StudentFee> findByStatusInAndDueDateBefore(List<FeeStatus> statuses, LocalDate date);

	long countByStatus(FeeStatus status);

	@Query("SELECT COALESCE(SUM(sf.amount), 0) FROM StudentFee sf")
	BigDecimal sumAllAmounts();

	@Query("SELECT COALESCE(SUM(sf.paidAmount), 0) FROM StudentFee sf")
	BigDecimal sumAllPaidAmounts();

	@Query("SELECT COALESCE(SUM(sf.dueAmount), 0) FROM StudentFee sf WHERE sf.status IN :statuses")
	BigDecimal sumDueAmountsByStatuses(@Param("statuses") List<FeeStatus> statuses);

	@Query("SELECT COALESCE(SUM(sf.amount), 0) FROM StudentFee sf WHERE sf.student.id = :studentId")
	BigDecimal sumAmountByStudent(@Param("studentId") Long studentId);

	@Query("SELECT COALESCE(SUM(sf.paidAmount), 0) FROM StudentFee sf WHERE sf.student.id = :studentId")
	BigDecimal sumPaidByStudent(@Param("studentId") Long studentId);

	@Query("SELECT COALESCE(SUM(sf.dueAmount), 0) FROM StudentFee sf WHERE sf.student.id = :studentId AND sf.status IN :statuses")
	BigDecimal sumDueByStudentAndStatuses(@Param("studentId") Long studentId, @Param("statuses") List<FeeStatus> statuses);

	List<StudentFee> findByStudent_IdOrderByFeeDateDesc(Long studentId);

	@Query("SELECT sf.status as status, COUNT(sf) as count FROM StudentFee sf GROUP BY sf.status")
	List<Object[]> countGroupByStatus();

	@Query("""
			SELECT COALESCE(SUM(sf.amount), 0), COALESCE(SUM(sf.paidAmount), 0), COALESCE(SUM(sf.dueAmount), 0), COUNT(sf)
			FROM StudentFee sf
			WHERE (:classId IS NULL OR sf.student.schoolClass.id = :classId)
			  AND ((sf.feeDate IS NOT NULL AND sf.feeDate BETWEEN :startDate AND :endDate)
				OR (sf.dueDate IS NOT NULL AND sf.dueDate BETWEEN :startDate AND :endDate))""")
	Object[] sumAmountsForPeriod(@Param("classId") Long classId, @Param("startDate") LocalDate startDate,
			@Param("endDate") LocalDate endDate);

	@Query("""
			SELECT sf.status, COUNT(sf), COALESCE(SUM(sf.amount), 0), COALESCE(SUM(sf.paidAmount), 0), COALESCE(SUM(sf.dueAmount), 0)
			FROM StudentFee sf
			WHERE (:classId IS NULL OR sf.student.schoolClass.id = :classId)
			  AND ((sf.feeDate IS NOT NULL AND sf.feeDate BETWEEN :startDate AND :endDate)
				OR (sf.dueDate IS NOT NULL AND sf.dueDate BETWEEN :startDate AND :endDate))
			GROUP BY sf.status
			ORDER BY sf.status""")
	List<Object[]> sumGroupedByStatusForPeriod(@Param("classId") Long classId, @Param("startDate") LocalDate startDate,
			@Param("endDate") LocalDate endDate);

	@Query("""
			SELECT sf FROM StudentFee sf
			WHERE sf.status IN :statuses AND sf.dueDate IS NOT NULL AND sf.dueDate < :date
			  AND (:classId IS NULL OR sf.student.schoolClass.id = :classId)
			ORDER BY sf.dueDate ASC, sf.dueAmount DESC""")
	List<StudentFee> findDefaulterFees(@Param("statuses") List<FeeStatus> statuses, @Param("date") LocalDate date,
			@Param("classId") Long classId, Pageable pageable);

	@Query("SELECT COALESCE(SUM(sf.amount), 0) as total, COALESCE(SUM(sf.paidAmount), 0) as paid, COALESCE(SUM(sf.dueAmount), 0) as pending FROM StudentFee sf")
	Object[] sumAmountsAggregated();
}
