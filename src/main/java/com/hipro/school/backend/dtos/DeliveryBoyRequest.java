package com.hipro.school.backend.dtos;

import com.hipro.school.backend.entities.Address;

import lombok.Data;

@Data
public class DeliveryBoyRequest {

	private String name;

	private String email;

	private String phone;
	
	private String password;

	private Integer franchiseId;
	
	private Address address;

}
