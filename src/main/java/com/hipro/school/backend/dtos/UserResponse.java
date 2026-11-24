package com.hipro.school.backend.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hipro.school.backend.entities.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {
	
	private Long id;

	private String fullName;

	private String email;

	private String phone;

	private Role role;

}
