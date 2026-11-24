package com.hipro.school.backend.dtos;

import java.math.BigDecimal;

import com.hipro.school.backend.enums.ConcessionType;
import com.hipro.school.backend.enums.DiscountType;

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
public class StudentConcessionRequest {

	@NotNull
	private Long studentId;

	private Long feeStructureId;

	@NotNull
	private ConcessionType type;

	@NotNull
	private DiscountType discountType;

	@NotNull
	@DecimalMin("0.01")
	@Digits(integer = 10, fraction = 2)
	private BigDecimal discountValue;

	@Size(max = 500)
	private String remarks;
}