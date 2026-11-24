package com.hipro.school.backend.dtos.transport;

import java.time.LocalDate;

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
public class StudentTransportResponse {

	private Long id;
	private Long studentId;
	private String studentName;
	private String admissionNumber;
	private Long routeId;
	private String routeName;
	private Long stopId;
	private String stopName;
	private LocalDate effectiveFrom;
	private String boardingPoint;
	private boolean active;
}
