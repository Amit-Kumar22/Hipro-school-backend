package com.hipro.school.backend.dtos.transport;

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
public class TransportDocumentRuleResponse {

	private Long id;
	private VehicleType vehicleType;
	private boolean requireInsurance;
	private boolean requireFitness;
	private boolean requirePermit;
	private boolean requirePuc;
	private int alertDaysBefore;
}
