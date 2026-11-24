package com.hipro.school.backend.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.hipro.school.backend.enums.SalaryStatus;

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
public class StaffSalaryResponse {

	private Long id;

	private Long userId;

	private String userName;

	private String userEmail;

	private Long globalTemplateId;

	private LocalDate salaryMonth;

	private BigDecimal basicSalary;

	private BigDecimal totalAllowances;

	private BigDecimal totalDeductions;

	private BigDecimal grossSalary;

	private BigDecimal netSalary;

	private Integer workingDays;

	private Integer paidDays;

	private Integer unpaidDays;

	private SalaryStatus status;

	private LocalDate paymentDate;

	private String paymentReference;

	private String remarks;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;
}
