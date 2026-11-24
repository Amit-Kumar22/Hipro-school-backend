package com.hipro.school.backend.dtos.transport;

import java.time.LocalDate;

import com.hipro.school.backend.enums.VehicleType;

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
public class TransportVehicleResponse {

	private Long id;
	private String registrationNumber;
	private VehicleType vehicleType;
	private String model;
	private Integer seatingCapacity;
	private LocalDate insuranceExpiry;
	private LocalDate fitnessExpiry;
	private LocalDate permitExpiry;
	private LocalDate pucExpiry;
	private String remarks;
	private boolean active;
}
