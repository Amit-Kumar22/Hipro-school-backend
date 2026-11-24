package com.hipro.school.backend.repositories;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hipro.school.backend.entities.TransportVehicleExpense;

@Repository
public interface TransportVehicleExpenseRepository
		extends JpaRepository<TransportVehicleExpense, Long>, JpaSpecificationExecutor<TransportVehicleExpense> {

	@Query("""
			SELECT COALESCE(SUM(e.amount), 0) FROM TransportVehicleExpense e
			WHERE e.expenseDate BETWEEN :from AND :to""")
	BigDecimal sumAmountForPeriod(@Param("from") LocalDate from, @Param("to") LocalDate to);

	@Query("""
			SELECT e.vehicle.id, e.vehicle.registrationNumber, COALESCE(SUM(e.amount), 0), COUNT(e)
			FROM TransportVehicleExpense e
			WHERE e.expenseDate BETWEEN :from AND :to
			GROUP BY e.vehicle.id, e.vehicle.registrationNumber ORDER BY SUM(e.amount) DESC""")
	List<Object[]> sumGroupedByVehicleForPeriod(@Param("from") LocalDate from, @Param("to") LocalDate to);

	@Query("""
			SELECT e.expenseType, COALESCE(SUM(e.amount), 0), COUNT(e) FROM TransportVehicleExpense e
			WHERE e.expenseDate BETWEEN :from AND :to
			GROUP BY e.expenseType ORDER BY SUM(e.amount) DESC""")
	List<Object[]> sumGroupedByTypeForPeriod(@Param("from") LocalDate from, @Param("to") LocalDate to);
}
