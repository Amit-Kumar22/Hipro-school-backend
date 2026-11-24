package com.hipro.school.backend.dtos;

import java.math.BigDecimal;

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
public class FeeGenerateResponse {

	private Long totalStudents;

	private int feeHeads;

	private int generated;

	private int skippedDuplicates;

	private BigDecimal totalAmountGenerated;
}
