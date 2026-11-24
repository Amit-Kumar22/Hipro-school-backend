package com.hipro.school.backend.dtos.transport;

import com.hipro.school.backend.enums.TransportDesignation;
import com.hipro.school.backend.enums.TransportEmploymentStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Returned once when an admin creates a driver. {@code temporaryPassword} is
 * shared with the driver out-of-band; it is never returned again.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverResponse {

	private Long staffId;
	private String name;
	private String mobileNumber;
	private Long userId;
	private String loginEmail;
	private String temporaryPassword;
	private TransportDesignation designation;
	private TransportEmploymentStatus employmentStatus;
	private Long assignedVehicleId;
	private boolean active;
}
