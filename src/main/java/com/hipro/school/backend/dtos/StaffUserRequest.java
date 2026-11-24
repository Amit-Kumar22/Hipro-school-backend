package com.hipro.school.backend.dtos;

import java.math.BigDecimal;
import java.util.List;

import com.hipro.school.backend.entities.Role;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StaffUserRequest {

	@NotBlank(message = "Name should not be empty")
	@Size(max = 100, message = "Name must not exceed 100 characters")
	private String name;

	@NotBlank(message = "Email should not be empty")
	@Email(message = "Invalid email address")
	@Size(max = 150, message = "Email must not exceed 150 characters")
	private String email;

	@NotBlank(message = "Phone number should not be empty")
	@Pattern(regexp = "^[0-9]{10,15}$", message = "Phone number must contain 10 to 15 digits")
	private String phoneNumber;

	@Size(max = 100, message = "Qualification must not exceed 100 characters")
	private String qualification;

	@DecimalMin(value = "0.0", inclusive = true, message = "Experience cannot be negative")
	@DecimalMax(value = "99.9", inclusive = true, message = "Invalid experience")
	private BigDecimal experienceYears;

	@NotNull(message = "Role is required")
	private Role role;

	private List<Long> classIds;

	private Long subjectId;
}