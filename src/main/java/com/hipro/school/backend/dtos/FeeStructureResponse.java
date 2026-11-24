package com.hipro.school.backend.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.hipro.school.backend.enums.FeeFrequency;
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
public class FeeStructureResponse {

	private Long id;
	
	private String title;

	private Long classId;

	private String className;

	private String academicYear;

	private FeeType feeType;
	
	private FeeFrequency frequency;

	private BigDecimal amount;

	private LocalDateTime createdAt;
}
