package com.hipro.school.backend.dtos;

import java.math.BigDecimal;

import com.hipro.school.backend.enums.FeeFrequency;
import com.hipro.school.backend.enums.FeeType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
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
public class FeeStructureRequest {

	@NotBlank(message = "Title is required")
	private String title;

	@NotNull(message = "Class ID is required")
	private Long classId;

	@NotNull(message = "Academic year is required")
	private String academicYear;
	
	private FeeFrequency frequency;

	@NotNull(message = "Fee type is required")
	private FeeType feeType;

	@NotNull(message = "Amount is required")
	@DecimalMin(value = "0.01", message = "Amount must be greater than 0")
	@Digits(integer = 10, fraction = 2)
	private BigDecimal amount;

	private String description;
}
