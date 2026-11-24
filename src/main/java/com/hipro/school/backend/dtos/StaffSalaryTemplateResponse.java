package com.hipro.school.backend.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
public class StaffSalaryTemplateResponse {

	private Long id;

	private BigDecimal basicSalary;

	private BigDecimal hra;

	private BigDecimal transportAllowance;

	private BigDecimal medicalAllowance;

	private BigDecimal otherAllowance;

	private BigDecimal pf;

	private BigDecimal professionalTax;

	private BigDecimal otherDeduction;

	private Boolean active;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;
}
