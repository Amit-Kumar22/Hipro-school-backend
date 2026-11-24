package com.hipro.school.backend.dtos;

import lombok.Data;

@Data
public class StudentResponse {

	private Integer id;

	private String fullName;

	private String email;

	private String phone;

}
