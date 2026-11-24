package com.hipro.school.backend.dtos;

import com.hipro.school.backend.entities.Address;
import com.hipro.school.backend.entities.Role;

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
public class AdminUserDto {

	private Long id;

	private Address address;

	@NotEmpty
	private String name;

	private Role role;

	@NotEmpty(message = "Email should not be empty")
	@Email
	private String email;

	@NotEmpty(message = "Phone Number should not be empty")
	private String phoneNumber;

	@NotEmpty(message = "password should not be empty")
	private String password;

}
