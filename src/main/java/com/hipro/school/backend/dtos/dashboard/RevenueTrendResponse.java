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
@Schema(description = "Monthly revenue data point for the dashboard")
public class RevenueTrendResponse {

	@Schema(description = "Month (1-12)")
	private int month;

	@Schema(description = "Year")
	private int year;

	@Schema(description = "Month name (e.g., January)")
	private String monthName;

	@Schema(description = "Total revenue collected in the month")
	private BigDecimal revenue;

	@Schema(description = "Number of payments in the month")
	private long paymentCount;
}
