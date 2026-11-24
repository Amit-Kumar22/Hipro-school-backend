package com.hipro.school.backend.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "employee_devices", indexes = { @Index(name = "idx_employee_device_employee", columnList = "employee_id"),
		@Index(name = "idx_employee_device_device_id", columnList = "device_id"),
		@Index(name = "idx_employee_device_active", columnList = "active"),
		@Index(name = "idx_employee_device_employee_active", columnList = "employee_id, active") }, uniqueConstraints = {
				@UniqueConstraint(name = "uk_employee_device_device_id", columnNames = "device_id") })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeDevice extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "employee_id", nullable = false, foreignKey = @ForeignKey(name = "fk_employee_device_employee"))
	private User employee;

	// Application-generated unique installation/device identifier.
	// Do not use this as a hardware identifier.
	@Column(name = "device_id", nullable = false, unique = true, length = 100)
	private String deviceId;

	// Device display name. Example: "Upendra's iPhone"
	@Column(name = "device_name", length = 150)
	private String deviceName;

	// Device model. Example: "iPhone 15 Pro", "SM-S928B"
	@Column(name = "model_name", length = 100)
	private String modelName;

	// Manufacturer. Example: Apple, Samsung, Xiaomi
	@Column(name = "manufacturer", length = 100)
	private String manufacturer;

	// Mobile platform. Example: IOS, ANDROID
	@Column(name = "platform", nullable = false, length = 20)
	private String platform;

	// Operating system version.
	@Column(name = "os_version", length = 50)
	private String osVersion;

	// Application version installed on device.
	@Column(name = "app_version", length = 50)
	private String appVersion;

	// Whether this device is currently allowed to access the employee account.
	@Column(name = "active", nullable = false)
	@Builder.Default
	private Boolean active = true;

	// Device registration time.
	@Column(name = "registered_at", nullable = false, updatable = false)
	private LocalDateTime registeredAt;

	// Last successful API/activity time from this device.
	@Column(name = "last_used_at")
	private LocalDateTime lastUsedAt;

	// Device deactivation/revocation time.
	@Column(name = "revoked_at")
	private LocalDateTime revokedAt;

	// Reason for device revocation.
	@Column(name = "revocation_reason", length = 255)
	private String revocationReason;

	// Last IP address used by this device.
	@Column(name = "last_ip_address", length = 45)
	private String lastIpAddress;

	// Optional cryptographic public key.
	// Recommended if you later implement device-level request signing.
	@Column(name = "public_key", columnDefinition = "TEXT")
	private String publicKey;

	// Number of successful authentication attempts.
	@Column(name = "successful_login_count", nullable = false)
	@Builder.Default
	private Long successfulLoginCount = 0L;

	// Number of failed authentication attempts.
	@Column(name = "failed_login_count", nullable = false)
	@Builder.Default
	private Long failedLoginCount = 0L;

	@PrePersist
	protected void onCreate() {
		LocalDateTime now = LocalDateTime.now();

		if (registeredAt == null) {
			registeredAt = now;
		}

		if (active == null) {
			active = true;
		}

		if (successfulLoginCount == null) {
			successfulLoginCount = 0L;
		}

		if (failedLoginCount == null) {
			failedLoginCount = 0L;
		}
	}

}