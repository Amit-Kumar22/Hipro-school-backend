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
@Schema(description = "Fee collection overview for the dashboard")
public class FeeOverviewResponse {

	@Schema(description = "Total fee amount generated")
	private BigDecimal totalFeeAmount;

	@Schema(description = "Total amount collected")
	private BigDecimal totalCollectedAmount;

	@Schema(description = "Total pending amount")
	private BigDecimal totalPendingAmount;

	@Schema(description = "Total overdue amount")
	private BigDecimal totalOverdueAmount;

	@Schema(description = "Number of paid student fees")
	private long paidCount;

	@Schema(description = "Number of pending student fees")
	private long pendingCount;

	@Schema(description = "Number of partial student fees")
	private long partialCount;

	@Schema(description = "Number of overdue student fees")
	private long overdueCount;

	@Schema(description = "Collection percentage (collected / total * 100)")
	private double collectionPercentage;
}
