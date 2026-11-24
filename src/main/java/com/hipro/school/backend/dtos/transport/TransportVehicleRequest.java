package com.hipro.school.backend.dtos.transport;

import java.time.LocalDate;

import com.hipro.school.backend.enums.VehicleType;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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
public class TransportVehicleRequest {

	@NotBlank(message = "Vehicle registration number is required")
	@Size(max = 20)
	private String registrationNumber;

	@NotNull(message = "Vehicle type is required")
	private VehicleType vehicleType;

	@Size(max = 100)
	private String model;

	@NotNull(message = "Seating capacity is required")
	@Min(value = 1, message = "Seating capacity must be at least 1")
	private Integer seatingCapacity;

	private LocalDate insuranceExpiry;

	private LocalDate fitnessExpiry;

	private LocalDate permitExpiry;

	private LocalDate pucExpiry;

	@Size(max = 500)
	private String remarks;
}
