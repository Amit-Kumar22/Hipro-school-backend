package com.hipro.school.backend.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.hipro.school.backend.enums.FeeStatus;
import com.hipro.school.backend.enums.FeeType;

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
public class StudentFeeResponse {

	private Long id;

	private Long studentId;

	private String studentName;

	private Long classId;

	private String className;

	private Long feeStructureId;

	private FeeType feeType;

	private BigDecimal amount;

	private BigDecimal paidAmount;

	private BigDecimal dueAmount;

	private LocalDate dueDate;

	private FeeStatus status;
}
