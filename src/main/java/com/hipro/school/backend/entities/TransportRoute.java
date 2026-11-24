package com.hipro.school.backend.entities;

import java.math.BigDecimal;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
@Table(name = "transport_routes", uniqueConstraints = {
		@UniqueConstraint(name = "uk_route_name", columnNames = { "route_name" }) }, indexes = {
				@Index(name = "idx_route_vehicle", columnList = "vehicle_id"),
				@Index(name = "idx_route_driver", columnList = "driver_id") })
public class TransportRoute extends BaseEntity {

	@NotBlank(message = "Route name is required")
	@Size(max = 100)
	@Column(name = "route_name", nullable = false, length = 100)
	private String routeName;

	@NotNull(message = "Transport fee is required")
	@DecimalMin(value = "0.00", message = "Transport fee must not be negative")
	@Digits(integer = 10, fraction = 2)
	@Column(name = "transport_fee", nullable = false, precision = 12, scale = 2)
	private BigDecimal transportFee;

	@Column(name = "pickup_time")
	private LocalTime pickupTime;

	@Column(name = "drop_time")
	private LocalTime dropTime;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "vehicle_id", foreignKey = @ForeignKey(name = "fk_route_vehicle"))
	private TransportVehicle vehicle;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "driver_id", foreignKey = @ForeignKey(name = "fk_route_driver"))
	private TransportStaff driver;

	@Size(max = 500)
	@Column(name = "description", length = 500)
	private String description;
}
