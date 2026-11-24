package com.hipro.school.backend.entities;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
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
@Table(name = "teachers", uniqueConstraints = {
		@UniqueConstraint(name = "uk_teacher_employee_code", columnNames = "employee_code") }, indexes = {
				@Index(name = "idx_teacher_employee_code", columnList = "employee_code") })
public class Teacher extends BaseEntity {

	@OneToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "teacher_user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_teacher_teacher_user"))
	private User teacherUser;

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "subject_id", nullable = false, foreignKey = @ForeignKey(name = "fk_teacher_subject"))
	private Subject subject;

	@Size(max = 100)
	@Column(name = "qualification", length = 100)
	private String qualification;

	// Total teaching experience in years. Example: 5.5 years
	@Column(name = "experience_years", precision = 4, scale = 1)
	private BigDecimal experienceYears;

	@NotBlank
	@Size(max = 30)
	@Column(name = "employee_code", nullable = false, length = 30)
	private String employeeCode;

	// Classes assigned to this teacher.
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "teacher_classes", joinColumns = @JoinColumn(name = "teacher_id", nullable = false, foreignKey = @ForeignKey(name = "fk_teacher_class_teacher")), inverseJoinColumns = @JoinColumn(name = "class_id", nullable = false, foreignKey = @ForeignKey(name = "fk_teacher_class_class")), uniqueConstraints = {
			@UniqueConstraint(name = "uk_teacher_class", columnNames = { "teacher_id", "class_id" }) }, indexes = {
					@Index(name = "idx_teacher_classes_teacher", columnList = "teacher_id"),
					@Index(name = "idx_teacher_classes_class", columnList = "class_id") })
	private Set<SchoolClass> assignedClasses = new HashSet<>();
}