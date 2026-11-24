package com.hipro.school.backend.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.hipro.school.backend.enums.PaymentMode;
import com.hipro.school.backend.enums.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {

	private Long id;

	private String receiptNumber;

	private Long studentId;

	private Long studentFeeId;

	private BigDecimal totalAmount;

	private PaymentMode paymentMode;

	private String transactionId;

	private String remark;

	private PaymentStatus status;

	private LocalDateTime paidAt;
}
