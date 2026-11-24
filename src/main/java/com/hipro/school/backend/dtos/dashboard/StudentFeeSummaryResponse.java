package com.hipro.school.backend.dtos.dashboard;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Student fee summary for dashboard")
public class StudentFeeSummaryResponse {

	@Schema(description = "Total fee amount")
	private BigDecimal totalFees;

	@Schema(description = "Total paid amount")
	private BigDecimal paidAmount;

	@Schema(description = "Total pending amount")
	private BigDecimal pendingAmount;

	@Schema(description = "Number of paid fees")
	private long paidCount;

	@Schema(description = "Number of pending fees")
	private long pendingCount;

	@Schema(description = "Number of overdue fees")
	private long overdueCount;

	@Schema(description = "Collection percentage")
	private double collectionPercentage;
}
