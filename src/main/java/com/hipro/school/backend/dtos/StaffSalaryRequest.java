package com.hipro.school.backend.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.hipro.school.backend.enums.SalaryStatus;

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
public class StaffSalaryRequest {

	@NotNull(message = "User ID is required")
	private Long userId;

	private Long globalTemplateId;

	@NotNull(message = "Salary month is required")
	private LocalDate salaryMonth;

	@NotNull(message = "Basic salary is required")
	@DecimalMin(value = "0.01", message = "Basic salary must be greater than 0")
	@Digits(integer = 10, fraction = 2)
	private BigDecimal basicSalary;

	private Integer workingDays;

	private Integer paidDays;

	private Integer unpaidDays;

	private SalaryStatus status;

	private LocalDate paymentDate;

	private String paymentReference;

	private String remarks;
}
