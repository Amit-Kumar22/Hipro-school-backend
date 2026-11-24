package com.hipro.school.backend.dtos.transport;

import java.time.LocalTime;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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
public class TransportRouteStopRequest {

	private Long id;

	@NotBlank(message = "Stop name is required")
	@Size(max = 150)
	private String stopName;

	@Min(value = 0)
	private Integer stopOrder;

	private LocalTime pickupTime;
}
