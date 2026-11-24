package com.hipro.school.backend.entities;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.hipro.school.backend.enums.FeeStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "student_fee", uniqueConstraints = {
		@UniqueConstraint(name = "uk_student_fee", columnNames = { "student_id", "fee_structure_id" }) }, indexes = {
				@Index(name = "idx_student_fee_student", columnList = "student_id"),
				@Index(name = "idx_student_fee_status", columnList = "status"),
				@Index(name = "idx_student_fee_fee_date", columnList = "fee_date"),
				@Index(name = "idx_student_fee_due_date", columnList = "due_date") })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentFee extends BaseEntity {

	@ManyToOne
	@JoinColumn(name = "student_id", nullable = false)
	private Student student;

	@ManyToOne
	@JoinColumn(name = "fee_structure_id", nullable = false)
	private FeeStructure feeStructure;

	@Digits(integer = 10, fraction = 2)
	@Column(precision = 12, scale = 2)
	private BigDecimal amount;

	@Digits(integer = 10, fraction = 2)
	@Builder.Default
	@Column(precision = 12, scale = 2)
	private BigDecimal paidAmount = BigDecimal.ZERO;

	@Digits(integer = 10, fraction = 2)
	@Column(precision = 12, scale = 2)
	private BigDecimal dueAmount;

	private LocalDate dueDate;

	private LocalDate feeDate;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private FeeStatus status;
}
