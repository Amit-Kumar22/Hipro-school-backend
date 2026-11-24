package com.hipro.school.backend.dtos.transport;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

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
public class TransportRouteResponse {

	private Long id;
	private String routeName;
	private BigDecimal transportFee;
	private LocalTime pickupTime;
	private LocalTime dropTime;
	private Long vehicleId;
	private String vehicleNumber;
	private Integer seatingCapacity;
	private Long driverId;
	private String driverName;
	private String description;
	private long assignedStudentCount;
	private boolean active;
	private List<TransportRouteStopResponse> stops;
}
