package com.hipro.school.backend.dtos.transport;

import com.hipro.school.backend.enums.VehicleType;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
public class TransportDocumentRuleRequest {

	@NotNull(message = "Vehicle type is required")
	private VehicleType vehicleType;

	private boolean requireInsurance = true;

	private boolean requireFitness = true;

	private boolean requirePermit = true;

	private boolean requirePuc = true;

	@Min(value = 1, message = "Alert days must be at least 1")
	private int alertDaysBefore = 30;
}
