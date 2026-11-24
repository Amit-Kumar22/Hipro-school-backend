package com.hipro.school.backend.dtos;

import com.hipro.school.backend.entities.Role;

import lombok.Data;

@Data
public class CustomerResponse {

	private Integer id;

	private String fullName;

	private String email;

	private String phone;
	
	private Role role;

}
