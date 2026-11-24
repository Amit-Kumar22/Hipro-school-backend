package com.hipro.school.backend.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssignBookingRequest {

	@NotNull(message = "driver is required.")
	private Integer driverId;

	@NotNull(message = "booking Detail is required.")
	private Integer bookingId;

}
