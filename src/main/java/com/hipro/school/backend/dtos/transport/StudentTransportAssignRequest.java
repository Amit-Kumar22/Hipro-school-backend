package com.hipro.school.backend.dtos.transport;

import java.time.LocalDate;

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
public class StudentTransportAssignRequest {

	@NotNull(message = "Student id is required")
	private Long studentId;

	@NotNull(message = "Route id is required")
	private Long routeId;

	private Long stopId;

	private LocalDate effectiveFrom;

	@Size(max = 200)
	private String boardingPoint;
}
