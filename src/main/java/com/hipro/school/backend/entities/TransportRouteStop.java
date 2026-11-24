package com.hipro.school.backend.entities;

import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
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
@Table(name = "transport_route_stops", indexes = {
		@Index(name = "idx_stop_route", columnList = "route_id") })
public class TransportRouteStop extends BaseEntity {

	@NotNull(message = "Route is required")
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "route_id", nullable = false, foreignKey = @ForeignKey(name = "fk_stop_route"))
	private TransportRoute route;

	@NotBlank(message = "Stop name is required")
	@Size(max = 150)
	@Column(name = "stop_name", nullable = false, length = 150)
	private String stopName;

	@Min(value = 0, message = "Stop order must not be negative")
	@Column(name = "stop_order")
	private Integer stopOrder = 0;

	@Column(name = "pickup_time")
	private LocalTime pickupTime;
}
