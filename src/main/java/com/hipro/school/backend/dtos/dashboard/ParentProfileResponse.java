package com.hipro.school.backend.dtos.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Parent profile information for dashboard")
public class ParentProfileResponse {

	@Schema(description = "Parent user ID")
	private Long userId;

	@Schema(description = "Parent full name")
	private String fullName;

	@Schema(description = "Email")
	private String email;

	@Schema(description = "Phone number")
	private String phone;

	@Schema(description = "Number of children")
	private int numberOfChildren;
}
