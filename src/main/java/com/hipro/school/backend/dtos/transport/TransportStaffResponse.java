package com.hipro.school.backend.dtos.transport;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.hipro.school.backend.enums.TransportDesignation;
import com.hipro.school.backend.enums.TransportEmploymentStatus;

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
public class TransportStaffResponse {

	private Long id;
	private String name;
	private String mobileNumber;
	private String address;
	private LocalDate joiningDate;
	private TransportDesignation designation;
	private String licenceNumber;
	private LocalDate licenceExpiry;
	private boolean licenceExpired;
	private BigDecimal monthlySalary;
	private TransportEmploymentStatus employmentStatus;
	private Long assignedVehicleId;
	private String assignedVehicleNumber;
	private Long userId;
	private String loginEmail;
	private boolean active;
}
