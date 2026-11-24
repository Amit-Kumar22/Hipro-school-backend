package com.hipro.school.backend.dtos.transport;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
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
public class TransportRouteRequest {

	@NotBlank(message = "Route name is required")
	@Size(max = 100)
	private String routeName;

	@NotNull(message = "Transport fee is required")
	@DecimalMin(value = "0.00", message = "Transport fee must not be negative")
	@Digits(integer = 10, fraction = 2)
	private BigDecimal transportFee;

	private LocalTime pickupTime;

	private LocalTime dropTime;

	private Long vehicleId;

	private Long driverId;

	@Size(max = 500)
	private String description;

	@Valid
	private List<TransportRouteStopRequest> stops;
}
