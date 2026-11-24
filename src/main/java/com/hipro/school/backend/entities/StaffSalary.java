package com.hipro.school.backend.entities;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.hipro.school.backend.enums.SalaryStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "staff_salaries", uniqueConstraints = {
		@UniqueConstraint(name = "uk_user_salary_month", columnNames = { "user_id", "salary_month" }) }, indexes = {
				@Index(name = "idx_staff_salary_user", columnList = "user_id"),
				@Index(name = "idx_user_salary_month", columnList = "salary_month"),
				@Index(name = "idx_user_salary_status", columnList = "status") })
public class StaffSalary extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_teacher_salary_user"))
	private User user;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "global_template_id", nullable = false, foreignKey = @ForeignKey(name = "fk_user_salary_template_global"))
	private StaffSalaryTemplate globalTemplate;

	@Column(name = "salary_month", nullable = false)
	private LocalDate salaryMonth;

	@Column(name = "basic_salary", precision = 12, scale = 2, nullable = false)
	private BigDecimal basicSalary;

	@Column(name = "total_allowances", precision = 12, scale = 2, nullable = false)
	private BigDecimal totalAllowances;

	@Column(name = "total_deductions", precision = 12, scale = 2, nullable = false)
	private BigDecimal totalDeductions;

	@Column(name = "gross_salary", precision = 12, scale = 2, nullable = false)
	private BigDecimal grossSalary;

	@Column(name = "net_salary", precision = 12, scale = 2, nullable = false)
	private BigDecimal netSalary;

	@Column(name = "working_days")
	private Integer workingDays;

	@Column(name = "paid_days")
	private Integer paidDays;

	@Column(name = "unpaid_days")
	private Integer unpaidDays;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 20)
	private SalaryStatus status;

	@Column(name = "payment_date")
	private LocalDate paymentDate;

	@Column(name = "payment_reference", length = 100)
	private String paymentReference;

	@Column(name = "remarks", length = 500)
	private String remarks;
}