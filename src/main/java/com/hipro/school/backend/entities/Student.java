package com.hipro.school.backend.entities;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hipro.school.backend.enums.BloodGroup;
import com.hipro.school.backend.enums.Gender;

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
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
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
@Table(name = "students", uniqueConstraints = {
		@UniqueConstraint(name = "uk_student_admission_number", columnNames = "admission_number") }, indexes = {
				@Index(name = "idx_student_admission", columnList = "admission_number"),
				@Index(name = "idx_student_user", columnList = "student_user_id"),
				@Index(name = "idx_student_class", columnList = "class_id"),
				@Index(name = "idx_student_roll_number", columnList = "roll_number"),
				@Index(name = "idx_student_admission_date", columnList = "admission_date") })
public class Student extends BaseEntity {

	// Student login/account.
	@OneToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "student_user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_student_student_user"))
	private User studentUser;

	// Parent login/account.
	@OneToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "parent_user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_student_parent_user"))
	private User parentUser;
	
	
	@Size(max = 100)
	@Column(name = "stud_image", length = 100)
	private String studentImage;

	// Unique admission number of the student.
	@NotBlank
	@Size(max = 50)
	@Column(name = "admission_number", nullable = false, length = 50)
	private String admissionNumber;

	@Size(max = 50)
	@Column(name = "student_code", length = 50)
	private String studentCode;

	// Academic year in which student is currently enrolled.
	// Prefer AcademicYear entity relationship for a multi-year production system.
	@Size(max = 20)
	@Column(name = "academic_year", length = 20)
	private String academicYear;

	// Roll number within the class/section.
	@Size(max = 20)
	@Column(name = "roll_number", length = 20)
	private String rollNumber;

	// Current class of the student.
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "class_id", nullable = false, foreignKey = @ForeignKey(name = "fk_student_class"))
	private SchoolClass schoolClass;

	// NEVER store raw passwords in production.
	// Authentication credentials should be stored in User using a properly hashed
	@JsonIgnore
	@Column(name = "raw_password")
	private String rawPassword;

	@Column(name = "admission_date", nullable = false)
	private LocalDate admissionDate;

	@Enumerated(EnumType.STRING)
	@Column(name = "blood_group", length = 20)
	private BloodGroup bloodGroup;

	@Column(name = "date_of_birth")
	private LocalDate dob;

	@Size(max = 500, message = "Address must not exceed 500 characters")
	@Column(name = "address", length = 500)
	private String address;

	@Pattern(regexp = "^[0-9]{6}$", message = "Pincode must contain exactly 6 digits")
	@Column(name = "pincode", length = 6)
	private String pincode;

	@Enumerated(EnumType.STRING)
	@Column(name = "gender", length = 20)
	private Gender gender;

	// Aadhaar number of the student (12 digits). Mapped from
	// StudentFormRequest.adharNumber.
	@Size(max = 20)
	@Column(name = "adhar_number", length = 20, unique = true)
	private String adharNumber;

	@Size(max = 100)
	@Column(name = "mother_name", length = 100)
	private String motherName;

	@Column(name = "mother_phone", length = 10)
	private String motherPhone;

	@Size(max = 150)
	@Column(name = "mother_email", length = 150)
	private String motherEmail;

	@Size(max = 100)
	@Column(name = "mother_occupation", length = 100)
	private String motherOccupation;

	@Size(max = 100)
	@Column(name = "father_name", length = 100)
	private String fatherName;

	@Size(max = 100)
	@Column(name = "father_occupation", length = 100)
	private String fatherOccupation;

	// Caste/category of the student (e.g. GEN/OBC/SC/ST). Mapped from
	// StudentFormRequest.cast.
	@Size(max = 50)
	@Column(name = "caste", length = 50)
	private String caste;

	@Size(max = 100)
	@Column(name = "nationality", length = 100)
	private String nationality;

	@Size(max = 50)
	@Column(name = "parent_annual_income", length = 50)
	private String parentAnnualIncome;
}