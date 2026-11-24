package com.hipro.school.backend.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserDto {

	private Long id;
	
	private String clientId;

	private String companyName;

	private String address;

	@NotEmpty
	private String name;

	@NotEmpty(message = "Email should not be empty")
	@Email
	private String email;

	@NotEmpty(message = "Phone Number should not be empty")
	private String phoneNumber;

	@NotEmpty(message = "password should not be empty")
	private String password;

}
