package com.hipro.school.backend.entities;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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
@Table(name = "student_transport_assignments", uniqueConstraints = {
		@UniqueConstraint(name = "uk_student_transport", columnNames = { "student_id" }) }, indexes = {
				@Index(name = "idx_stransport_student", columnList = "student_id"),
				@Index(name = "idx_stransport_route", columnList = "route_id"),
				@Index(name = "idx_stransport_stop", columnList = "stop_id") })
public class StudentTransportAssignment extends BaseEntity {

	@NotNull(message = "Student is required")
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "student_id", nullable = false, foreignKey = @ForeignKey(name = "fk_stransport_student"))
	private Student student;

	@NotNull(message = "Route is required")
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "route_id", nullable = false, foreignKey = @ForeignKey(name = "fk_stransport_route"))
	private TransportRoute route;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "stop_id", foreignKey = @ForeignKey(name = "fk_stransport_stop"))
	private TransportRouteStop stop;

	@Column(name = "effective_from")
	private LocalDate effectiveFrom;

	@Size(max = 200)
	@Column(name = "boarding_point", length = 200)
	private String boardingPoint;
}
