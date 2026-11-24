package com.hipro.school.backend.dtos;

import java.time.LocalDate;

import com.hipro.school.backend.enums.Gender;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class StudentImportRow {

	@NotBlank(message = "Name is required")
	@Size(max = 100, message = "Name must not exceed 100 characters")
	private String name;

	@Size(max = 100, message = "Father name must not exceed 100 characters")
	private String fatherName;

	@Size(max = 100, message = "Mother name must not exceed 100 characters")
	private String motherName;

	@Email(message = "Invalid parent email")
	@Size(max = 150, message = "Parent email must not exceed 150 characters")
	private String parentEmail;

	@NotBlank(message = "Parent phone number is required")
	@Pattern(regexp = "^[6-9][0-9]{9}$", message = "Invalid parent phone number")
	private String parentPhone;

	@NotBlank(message = "Class name is required")
	private String className;

	@Past(message = "Date of birth must be in the past")
	private LocalDate dob;

	@Size(max = 500, message = "Address must not exceed 500 characters")
	private String address;

	@Pattern(regexp = "^[0-9]{6}$", message = "Pincode must contain exactly 6 digits")
	private String pincode;

	private Gender gender;

}
