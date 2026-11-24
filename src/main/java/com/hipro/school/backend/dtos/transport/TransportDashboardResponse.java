package com.hipro.school.backend.dtos.transport;

import java.math.BigDecimal;
import java.util.List;

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
public class TransportDashboardResponse {

	private long totalVehicles;
	private long activeVehicles;
	private long activeDrivers;
	private long activeRoutes;
	private long transportStudents;
	private BigDecimal monthlySalaryExpense;
	private BigDecimal monthlyVehicleExpense;
	private BigDecimal outstandingTransportFees;
	private long expiredDocumentCount;
	private List<DocumentExpiryAlert> upcomingExpiries;
}
