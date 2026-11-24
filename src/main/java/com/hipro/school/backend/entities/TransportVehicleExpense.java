package com.hipro.school.backend.entities;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.hipro.school.backend.enums.PaymentMode;
import com.hipro.school.backend.enums.VehicleExpenseType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transport_vehicle_expenses", indexes = {
		@Index(name = "idx_vexpense_vehicle", columnList = "vehicle_id"),
		@Index(name = "idx_vexpense_date", columnList = "expense_date"),
		@Index(name = "idx_vexpense_type", columnList = "expense_type") })
public class TransportVehicleExpense extends BaseEntity {

	@NotNull(message = "Vehicle is required")
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "vehicle_id", nullable = false, foreignKey = @ForeignKey(name = "fk_vexpense_vehicle"))
	private TransportVehicle vehicle;

	@NotNull(message = "Expense date is required")
	@Column(name = "expense_date", nullable = false)
	private LocalDate expenseDate;

	@NotNull(message = "Expense type is required")
	@Enumerated(EnumType.STRING)
	@Column(name = "expense_type", nullable = false, length = 30)
	private VehicleExpenseType expenseType;

	@NotNull(message = "Amount is required")
	@DecimalMin(value = "0.01", message = "Amount must be greater than 0")
	@Digits(integer = 10, fraction = 2)
	@Column(name = "amount", nullable = false, precision = 12, scale = 2)
	private BigDecimal amount;

	@Column(name = "fuel_litres")
	private Double fuelLitres;

	@Column(name = "odometer_reading")
	private Long odometerReading;

	@Size(max = 150)
	@Column(name = "vendor", length = 150)
	private String vendor;

	@Size(max = 100)
	@Column(name = "bill_number", length = 100)
	private String billNumber;

	@Enumerated(EnumType.STRING)
	@Column(name = "payment_mode", length = 20)
	private PaymentMode paymentMode;

	@Size(max = 500)
	@Column(name = "remarks", length = 500)
	private String remarks;
}
