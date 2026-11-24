package com.hipro.school.backend.entities;

import java.time.LocalDate;

import com.hipro.school.backend.enums.VehicleType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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
@Table(name = "transport_vehicles", uniqueConstraints = {
		@UniqueConstraint(name = "uk_vehicle_reg_no", columnNames = { "registration_number" }) }, indexes = {
				@Index(name = "idx_vehicle_reg_no", columnList = "registration_number"),
				@Index(name = "idx_vehicle_type", columnList = "vehicle_type") })
public class TransportVehicle extends BaseEntity {

	@NotBlank(message = "Vehicle registration number is required")
	@Size(max = 20, message = "Registration number must not exceed 20 characters")
	@Column(name = "registration_number", nullable = false, length = 20)
	private String registrationNumber;

	@NotNull(message = "Vehicle type is required")
	@Enumerated(EnumType.STRING)
	@Column(name = "vehicle_type", nullable = false, length = 30)
	private VehicleType vehicleType;

	@Size(max = 100)
	@Column(name = "model", length = 100)
	private String model;

	@NotNull(message = "Seating capacity is required")
	@Min(value = 1, message = "Seating capacity must be at least 1")
	@Column(name = "seating_capacity", nullable = false)
	private Integer seatingCapacity;

	@Column(name = "insurance_expiry")
	private LocalDate insuranceExpiry;

	@Column(name = "fitness_expiry")
	private LocalDate fitnessExpiry;

	@Column(name = "permit_expiry")
	private LocalDate permitExpiry;

	@Column(name = "puc_expiry")
	private LocalDate pucExpiry;

	@Size(max = 500)
	@Column(name = "remarks", length = 500)
	private String remarks;
}
