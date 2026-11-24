package com.hipro.school.backend.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hipro.school.backend.entities.Address;
import com.hipro.school.backend.entities.Role;
import com.hipro.school.backend.enums.UserStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeliveryUserResponse {
	
	private Integer id;

	private String name;

	private String email;

	private String phone;
	
	private UserStatus status;

	private Address address;
	
	private Role role;

}
