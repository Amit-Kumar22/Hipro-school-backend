package com.hipro.school.backend.dtos.transport;

import java.time.LocalTime;

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
public class TransportRouteStopResponse {

	private Long id;
	private String stopName;
	private Integer stopOrder;
	private LocalTime pickupTime;
}
