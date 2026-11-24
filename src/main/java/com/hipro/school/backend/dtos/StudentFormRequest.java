package com.hipro.school.backend.dtos;

import java.time.LocalDate;

import com.hipro.school.backend.enums.Gender;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class StudentFormRequest {

	@NotNull(message = "Class is required")
	private Long classId;

	@Size(max = 20, message = "Session must not exceed 20 characters")
	private String session;

	@NotBlank(message = "Student name is required")
	@Size(max = 100, message = "Name must not exceed 100 characters")
	private String name;

	@Pattern(regexp = "^[0-9]{12}$", message = "Adhar number must contain exactly 12 digits")
	private String adharNumber;
	
	private String photo;

	private Gender gender;

	@Past(message = "Date of birth must be in the past")
	private LocalDate dob;

	@Size(max = 20, message = "Blood Group must not exceed 20 characters")
	private String bloodGroup;

	@Size(max = 50, message = "Cast must not exceed 50 characters")
	private String cast;

	@Size(max = 100, message = "Father name must not exceed 100 characters")
	private String fatherName;

	@Email(message = "Invalid father email")
	@Size(max = 150, message = "Father email must not exceed 150 characters")
	private String fatherEmail;

	@Pattern(regexp = "^[6-9][0-9]{9}$", message = "Invalid parent phone number")
	private String fatherPhone;

	@Size(max = 100, message = "Father Occupation must not exceed 100 characters")
	private String fatherOccupation;

	@Size(max = 100, message = "Mother name must not exceed 100 characters")
	private String motherName;

	@Email(message = "Invalid mother email")
	@Size(max = 150, message = "mother email must not exceed 150 characters")
	private String motherEmail;

	@Pattern(regexp = "^[6-9][0-9]{9}$", message = "Invalid mother phone number")
	private String motherPhone;

	@Size(max = 100, message = "Mother Occupation must not exceed 100 characters")
	private String motherOccupation;

	@Size(max = 100, message = "Nationality must not exceed 100 characters")
	private String nationality;

	@Size(max = 50, message = "Parent annual income must not exceed 50 characters")
	private String parentAnnualIncome;

	@Size(max = 500, message = "Address must not exceed 500 characters")
	private String address;

	@Pattern(regexp = "^[0-9]{6}$", message = "Pincode must contain exactly 6 digits")
	private String pincode;

}
