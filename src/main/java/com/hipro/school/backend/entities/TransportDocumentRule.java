package com.hipro.school.backend.entities;

import com.hipro.school.backend.enums.VehicleType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transport_document_rules", uniqueConstraints = {
		@UniqueConstraint(name = "uk_doc_rule_vehicle_type", columnNames = { "vehicle_type" }) }, indexes = {
				@Index(name = "idx_doc_rule_type", columnList = "vehicle_type") })
public class TransportDocumentRule extends BaseEntity {

	@Enumerated(EnumType.STRING)
	@Column(name = "vehicle_type", nullable = false, length = 30)
	private VehicleType vehicleType;

	@Column(name = "require_insurance", nullable = false)
	private boolean requireInsurance = true;

	@Column(name = "require_fitness", nullable = false)
	private boolean requireFitness = true;

	@Column(name = "require_permit", nullable = false)
	private boolean requirePermit = true;

	@Column(name = "require_puc", nullable = false)
	private boolean requirePuc = true;

	@Column(name = "alert_days_before", nullable = false)
	private int alertDaysBefore = 30;
}
