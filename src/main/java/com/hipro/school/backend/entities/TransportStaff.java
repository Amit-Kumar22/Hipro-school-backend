package com.hipro.school.backend.entities;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.hipro.school.backend.enums.TransportDesignation;
import com.hipro.school.backend.enums.TransportEmploymentStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transport_staff", indexes = {
		@Index(name = "idx_tstaff_designation", columnList = "designation"),
		@Index(name = "idx_tstaff_status", columnList = "employment_status"),
		@Index(name = "idx_tstaff_vehicle", columnList = "assigned_vehicle_id") })
public class TransportStaff extends BaseEntity {

	@NotBlank(message = "Staff name is required")
	@Size(max = 100)
	@Column(name = "name", nullable = false, length = 100)
	private String name;

	@NotBlank(message = "Mobile number is required")
	@Pattern(regexp = "^[0-9]{10}$", message = "Mobile number must contain exactly 10 digits")
	@Column(name = "mobile_number", nullable = false, length = 10)
	private String mobileNumber;

	@Size(max = 500)
	@Column(name = "address", length = 500)
	private String address;

	@Column(name = "joining_date")
	private LocalDate joiningDate;

	@NotNull(message = "Designation is required")
	@Enumerated(EnumType.STRING)
	@Column(name = "designation", nullable = false, length = 30)
	private TransportDesignation designation;

	@Size(max = 30)
	@Column(name = "licence_number", length = 30)
	private String licenceNumber;

	@Column(name = "licence_expiry")
	private LocalDate licenceExpiry;

	@DecimalMin(value = "0.00", message = "Monthly salary must not be negative")
	@Digits(integer = 10, fraction = 2)
	@Column(name = "monthly_salary", precision = 12, scale = 2)
	private BigDecimal monthlySalary;

	@Enumerated(EnumType.STRING)
	@Column(name = "employment_status", nullable = false, length = 20)
	private TransportEmploymentStatus employmentStatus = TransportEmploymentStatus.ACTIVE;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "assigned_vehicle_id", foreignKey = @ForeignKey(name = "fk_tstaff_vehicle"))
	private TransportVehicle assignedVehicle;

	// Login account of this staff member (present only for staff with app access,
	// e.g. drivers with the DRIVER role). Nullable so existing staff rows keep working.
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", unique = true, foreignKey = @ForeignKey(name = "fk_tstaff_user"))
	private User user;
}
