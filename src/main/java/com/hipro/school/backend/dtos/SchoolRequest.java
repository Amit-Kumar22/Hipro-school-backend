package com.hipro.school.backend.dtos;

import com.hipro.school.backend.entities.Address;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SchoolRequest {

	@NotBlank(message = "School name is required")
	@Size(min = 3, max = 150, message = "School name must be between 3 and 150 characters")
	private String schoolName;

	@NotBlank(message = "School code is required")
	@Pattern(regexp = "^[A-Z0-9_-]{3,20}$", message = "School code must be 3-20 characters and contain only uppercase letters, numbers, _ or -")
	private String schoolCode;

	@NotBlank(message = "Registration number is required")
	@Size(max = 50, message = "Registration number cannot exceed 50 characters")
	private String registrationNumber;

	@NotBlank(message = "Affiliation board is required")
	@Pattern(regexp = "^(CBSE|ICSE|STATE|IB|OTHER)$", message = "Affiliation board must be CBSE, ICSE, STATE, IB, or OTHER")
	private String affiliationBoard;

	@NotNull(message = "Established year is required")
	@Min(value = 1800, message = "Established year must be valid")
	@Max(value = 2100, message = "Established year must be valid")
	private Integer establishedYear;

	@NotBlank(message = "Email is required")
	@Email(message = "Invalid email format")
	@Size(max = 150)
	private String email;

	@NotBlank(message = "Phone number is required")
	@Pattern(regexp = "^[6-9][0-9]{9}$", message = "Phone number must be a valid 10 digit Indian mobile number")
	private String phoneNumber;

	@Pattern(regexp = "^[6-9][0-9]{9}$", message = "Alternate phone must be a valid 10 digit Indian mobile number")
	private String alternatePhone;

	@Pattern(regexp = "^(https?://).*$", message = "Website must be a valid URL starting with http or https")
	private String website;

	@Pattern(regexp = "^(https?://).*$", message = "Logo URL must be a valid URL")
	private String logoUrl;

	@NotBlank(message = "Principal name is required")
	@Size(min = 3, max = 120, message = "Principal name must be between 3 and 120 characters")
	private String principalName;

	@Min(value = 0, message = "Total students cannot be negative")
	@Max(value = 100000, message = "Total students value seems unrealistic")
	private Integer totalStudents;

	@Min(value = 0, message = "Total teachers cannot be negative")
	@Max(value = 10000, message = "Total teachers value seems unrealistic")
	private Integer totalTeachers;

	@NotNull(message = "Address is required")
	@Valid
	private Address address;
}