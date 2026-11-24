package com.hipro.school.backend.repositories;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hipro.school.backend.entities.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long>, JpaSpecificationExecutor<Payment> {

	List<Payment> findAllByStudentFee_IdOrderByPaidAtDesc(Long studentFeeId);

	@Query("SELECT COALESCE(SUM(p.totalAmount), 0) FROM Payment p WHERE p.status = com.hipro.school.backend.enums.PaymentStatus.SUCCESS AND p.paidAt BETWEEN :startDate AND :endDate")
	BigDecimal sumSuccessfulPaymentsBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

	@Query("SELECT COALESCE(SUM(p.totalAmount), 0) FROM Payment p WHERE p.status = com.hipro.school.backend.enums.PaymentStatus.SUCCESS")
	BigDecimal sumAllSuccessfulPayments();

	@Query("SELECT p FROM Payment p WHERE p.studentFee.student.id = :studentId ORDER BY p.paidAt DESC")
	List<Payment> findByStudentIdOrderByPaidAtDesc(@Param("studentId") Long studentId);

	@Query("SELECT YEAR(p.paidAt) as year, MONTH(p.paidAt) as month, COALESCE(SUM(p.totalAmount), 0) as revenue, COUNT(p) as paymentCount FROM Payment p WHERE p.status = com.hipro.school.backend.enums.PaymentStatus.SUCCESS AND p.paidAt >= :startDate GROUP BY YEAR(p.paidAt), MONTH(p.paidAt) ORDER BY year, month")
	List<Object[]> sumRevenueGroupByMonth(@Param("startDate") LocalDateTime startDate);
}
