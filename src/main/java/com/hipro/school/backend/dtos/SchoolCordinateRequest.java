package com.hipro.school.backend.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SchoolCordinateRequest {

	@NotNull(message = "Latitude is required")
	private Double latitude;

	@NotNull(message = "Longitude is required")
	private Double longitude;

	@NotNull(message = "allowed radius meters is required")
	private Double allowedRadiusMeters;
}