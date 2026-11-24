package com.hipro.school.backend.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.hipro.school.backend.enums.PaymentMode;
import com.hipro.school.backend.enums.PaymentStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "payments", uniqueConstraints = @UniqueConstraint(name = "uk_receipt", columnNames = "receipt_number"), indexes = {
		@Index(name = "idx_payment_student_fee", columnList = "student_fee_id"),
		@Index(name = "idx_payment_status", columnList = "status"),
		@Index(name = "idx_payment_paid_at", columnList = "paid_at"),
		@Index(name = "idx_payment_status_paid_at", columnList = "status, paid_at") })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment extends BaseEntity {

	@NotBlank
	@Size(max = 50)
	@Column(name = "receipt_number", nullable = false, length = 50)
	private String receiptNumber;

	// Optional link to the student fee this payment is allocated to.
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "student_fee_id")
	private StudentFee studentFee;

	@NotNull
	@DecimalMin("0.01")
	@Digits(integer = 10, fraction = 2)
	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal totalAmount;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private PaymentMode paymentMode;

	@Size(max = 100)
	private String transactionId;

	@Size(max = 255)
	private String remark;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private PaymentStatus status;

	@NotNull
	@Column(nullable = false)
	private LocalDateTime paidAt;
}
