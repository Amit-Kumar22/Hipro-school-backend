package com.hipro.school.backend.dtos.transport;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.hipro.school.backend.enums.PaymentMode;
import com.hipro.school.backend.enums.VehicleExpenseType;

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
public class TransportVehicleExpenseResponse {

	private Long id;
	private Long vehicleId;
	private String registrationNumber;
	private LocalDate expenseDate;
	private VehicleExpenseType expenseType;
	private BigDecimal amount;
	private Double fuelLitres;
	private Long odometerReading;
	private String vendor;
	private String billNumber;
	private PaymentMode paymentMode;
	private String remarks;
}
