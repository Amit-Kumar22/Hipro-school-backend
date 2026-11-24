package com.hipro.school.backend.repositories;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hipro.school.backend.entities.StaffSalary;
import com.hipro.school.backend.enums.SalaryStatus;

@Repository
public interface StaffSalaryRepository extends JpaRepository<StaffSalary, Long>, JpaSpecificationExecutor<StaffSalary> {

	boolean existsByUserIdAndSalaryMonth(Long userId, LocalDate salaryMonth);

	boolean existsByUserIdAndSalaryMonthAndIdNot(Long userId, LocalDate salaryMonth, Long id);

	Optional<StaffSalary> findByUserIdAndSalaryMonth(Long userId, LocalDate salaryMonth);

	@Query("""
			SELECT COALESCE(SUM(s.netSalary), 0) FROM StaffSalary s
			WHERE s.salaryMonth BETWEEN :from AND :to AND s.status IN :statuses
			AND EXISTS (SELECT 1 FROM TransportStaff ts WHERE ts.user.id = s.user.id)""")
	BigDecimal sumNetSalaryForTransportStaff(@Param("from") LocalDate from, @Param("to") LocalDate to,
			@Param("statuses") List<SalaryStatus> statuses);
}
