package com.hipro.school.backend.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hipro.school.backend.entities.Address;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserRequest {

	private String name;

	private String phone;

	private Address address;


}
