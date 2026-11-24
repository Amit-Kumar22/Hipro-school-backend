package com.hipro.school.backend.dtos.transport;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Admin creates a driver login + driver profile in a single request. All staff
 * fields are inherited from {@link TransportStaffRequest} so common details
 * (name, phone, address, ...) are entered only once; {@code email} becomes the
 * login id of the generated DRIVER user.
 */
@Getter
@Setter
@NoArgsConstructor
public class DriverRequest extends TransportStaffRequest {

	@NotBlank(message = "Login email is required")
	@Email(message = "Invalid email address")
	@Size(max = 150, message = "Email must not exceed 150 characters")
	private String email;
}
