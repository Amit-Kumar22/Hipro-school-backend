package com.hipro.school.backend.dtos.transport;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.hipro.school.backend.enums.PaymentMode;
import com.hipro.school.backend.enums.VehicleExpenseType;

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
public class TransportVehicleExpenseRequest {

	@NotNull(message = "Vehicle id is required")
	private Long vehicleId;

	@NotNull(message = "Expense date is required")
	private LocalDate expenseDate;

	@NotNull(message = "Expense type is required")
	private VehicleExpenseType expenseType;

	@NotNull(message = "Amount is required")
	@DecimalMin(value = "0.01", message = "Amount must be greater than 0")
	@Digits(integer = 10, fraction = 2)
	private BigDecimal amount;

	private Double fuelLitres;

	private Long odometerReading;

	@Size(max = 150)
	private String vendor;

	@Size(max = 100)
	private String billNumber;

	private PaymentMode paymentMode;

	@Size(max = 500)
	private String remarks;
}
