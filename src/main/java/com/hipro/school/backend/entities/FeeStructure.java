package com.hipro.school.backend.entities;

import java.math.BigDecimal;

import com.hipro.school.backend.enums.FeeFrequency;
import com.hipro.school.backend.enums.FeeType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "fee_structure", uniqueConstraints = {
		@UniqueConstraint(name = "uk_fee_structure", columnNames = { "class_id", "fee_type" }) }, indexes = {
				@Index(name = "idx_fee_class", columnList = "class_id") })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeeStructure extends BaseEntity {

	@Column(name = "title", nullable = false, length = 150)
	private String title;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "fee_type", nullable = false, length = 50)
	private FeeType feeType;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "frequency", nullable = false, length = 50)
	private FeeFrequency frequency;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "class_id", nullable = false)
	private SchoolClass classRoom;

	@NotNull
	@DecimalMin(value = "0.0", inclusive = false)
	@Digits(integer = 10, fraction = 2)
	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal amount;

	@Column(name = "academic_year", length = 50)
	private String academicYear;
	
	@Column(name = "description", length = 500)
	private String description;
}
