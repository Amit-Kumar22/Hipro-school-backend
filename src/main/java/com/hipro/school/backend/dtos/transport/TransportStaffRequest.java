package com.hipro.school.backend.dtos.transport;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.hipro.school.backend.enums.TransportDesignation;
import com.hipro.school.backend.enums.TransportEmploymentStatus;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
public class TransportStaffRequest {

	@NotBlank(message = "Staff name is required")
	@Size(max = 100)
	private String name;

	@NotBlank(message = "Mobile number is required")
	@Pattern(regexp = "^[0-9]{10}$", message = "Mobile number must contain exactly 10 digits")
	private String mobileNumber;

	@Size(max = 500)
	private String address;

	private LocalDate joiningDate;

	@NotNull(message = "Designation is required")
	private TransportDesignation designation;

	@Size(max = 30)
	private String licenceNumber;

	private LocalDate licenceExpiry;

	@DecimalMin(value = "0.00", message = "Monthly salary must not be negative")
	@Digits(integer = 10, fraction = 2)
	private BigDecimal monthlySalary;

	private TransportEmploymentStatus employmentStatus;

	private Long assignedVehicleId;
}
