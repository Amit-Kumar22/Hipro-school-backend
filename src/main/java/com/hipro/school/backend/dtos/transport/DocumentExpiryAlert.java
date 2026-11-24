package com.hipro.school.backend.dtos.transport;

import java.time.LocalDate;

import com.hipro.school.backend.enums.TransportDocumentType;

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
public class DocumentExpiryAlert {

	private Long vehicleId;
	private String registrationNumber;
	private TransportDocumentType documentType;
	private LocalDate expiryDate;
	private long daysRemaining;
	private boolean expired;
}
