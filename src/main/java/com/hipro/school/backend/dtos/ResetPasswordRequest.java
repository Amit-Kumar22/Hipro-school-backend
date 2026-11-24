package com.hipro.school.backend.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResetPasswordRequest {

	@NotBlank(message = "OldPassword must not be empty.")
	private String oldPassword;

	@NotBlank(message = "Newpassword must not be empty.")
	private String newPassword;

}
