package com.hipro.school.backend.dtos;

import java.math.BigDecimal;

import com.hipro.school.backend.enums.PaymentMode;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class FeePaymentRequest {

	@NotNull(message = "Amount is required")
	@DecimalMin(value = "0.01", message = "Amount must be greater than 0")
	@Digits(integer = 10, fraction = 2)
	private BigDecimal amount;

	@NotNull(message = "Payment mode is required")
	private PaymentMode paymentMode;

	@Size(max = 100)
	private String transactionId;

	@Size(max = 255)
	private String remark;
}
