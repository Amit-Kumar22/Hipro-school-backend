package com.hipro.school.backend.dtos;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
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
public class StaffSalaryTemplateRequest {

	@NotNull(message = "Basic salary is required")
	@DecimalMin(value = "0.01", message = "Basic salary must be greater than 0")
	@Digits(integer = 10, fraction = 2)
	private BigDecimal basicSalary;

	@DecimalMin(value = "0.00", message = "HRA must be zero or positive")
	@Digits(integer = 10, fraction = 2)
	private BigDecimal hra;

	@DecimalMin(value = "0.00", message = "Transport allowance must be zero or positive")
	@Digits(integer = 10, fraction = 2)
	private BigDecimal transportAllowance;

	@DecimalMin(value = "0.00", message = "Medical allowance must be zero or positive")
	@Digits(integer = 10, fraction = 2)
	private BigDecimal medicalAllowance;

	@DecimalMin(value = "0.00", message = "Other allowance must be zero or positive")
	@Digits(integer = 10, fraction = 2)
	private BigDecimal otherAllowance;

	@DecimalMin(value = "0.00", message = "PF must be zero or positive")
	@Digits(integer = 10, fraction = 2)
	private BigDecimal pf;

	@DecimalMin(value = "0.00", message = "Professional tax must be zero or positive")
	@Digits(integer = 10, fraction = 2)
	private BigDecimal professionalTax;

	@DecimalMin(value = "0.00", message = "Other deduction must be zero or positive")
	@Digits(integer = 10, fraction = 2)
	private BigDecimal otherDeduction;

	private Boolean active;
}
