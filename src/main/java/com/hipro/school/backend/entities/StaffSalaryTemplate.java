package com.hipro.school.backend.entities;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "staff_salary_templates")
public class StaffSalaryTemplate extends BaseEntity {

	@Column(name = "basic_salary", precision = 12, scale = 2, nullable = false)
	private BigDecimal basicSalary;

	@Column(name = "hra", precision = 12, scale = 2)
	private BigDecimal hra;

	@Column(name = "transport_allowance", precision = 12, scale = 2)
	private BigDecimal transportAllowance;

	@Column(name = "medical_allowance", precision = 12, scale = 2)
	private BigDecimal medicalAllowance;

	@Column(name = "other_allowance", precision = 12, scale = 2)
	private BigDecimal otherAllowance;

	@Column(name = "pf", precision = 12, scale = 2)
	private BigDecimal pf;

	@Column(name = "professional_tax", precision = 12, scale = 2)
	private BigDecimal professionalTax;

	@Column(name = "other_deduction", precision = 12, scale = 2)
	private BigDecimal otherDeduction;

	@Column(name = "active", nullable = false)
	private Boolean active = true;
}
