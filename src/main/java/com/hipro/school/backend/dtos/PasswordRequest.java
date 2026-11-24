package com.hipro.school.backend.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PasswordRequest {

	@NotBlank(message = "Password must not be empty.")
	private String password;

	@NotBlank(message = "Confirm password must not be empty.")
	private String confirmPassword;

	private String code;


}
