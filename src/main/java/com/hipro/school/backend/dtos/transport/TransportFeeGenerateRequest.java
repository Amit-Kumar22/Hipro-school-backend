package com.hipro.school.backend.dtos.transport;

import java.time.LocalDate;

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
public class TransportFeeGenerateRequest {

	@NotNull(message = "Student id is required")
	private Long studentId;

	@NotNull(message = "Route id is required")
	private Long routeId;

	private LocalDate feeDate;

	private LocalDate dueDate;

	private String academicYear;
}
