package com.hipro.school.backend.dtos;

import java.time.LocalDate;

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
public class FeeGenerateRequest {

	@NotNull(message = "Fee Structure ID is required")
	private Long feeStructureId;

	@NotNull(message = "StudentId ID is required")
	private Long studentId;

	private LocalDate feeDate;
}
