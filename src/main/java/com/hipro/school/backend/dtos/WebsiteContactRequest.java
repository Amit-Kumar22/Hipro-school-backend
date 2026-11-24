package com.hipro.school.backend.dtos;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebsiteContactRequest {

	@Size(max = 20, message = "Phone must not exceed 20 characters")
	private String phone;

	@Size(max = 150, message = "Email must not exceed 150 characters")
	private String email;

	@Size(max = 150, message = "Address line 1 must not exceed 150 characters")
	private String addressLine1;

	@Size(max = 150, message = "Address line 2 must not exceed 150 characters")
	private String addressLine2;

	@Size(max = 100, message = "City must not exceed 100 characters")
	private String city;

	@Size(max = 100, message = "State must not exceed 100 characters")
	private String state;

	@Size(max = 20, message = "Pincode must not exceed 20 characters")
	private String pincode;

	@Size(max = 100, message = "Country must not exceed 100 characters")
	private String country;

	@Size(max = 100, message = "Working days must not exceed 100 characters")
	private String workingDays;

	@Size(max = 100, message = "Working time must not exceed 100 characters")
	private String workingTime;
}
