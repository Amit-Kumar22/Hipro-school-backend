package com.hipro.school.backend.dtos;

import lombok.Data;

@Data
public class StudentRawRequest {

	private String rollNo;

	private String reg_no;

	private String StudentName;

	private String fatherName;

	private String motherName;

	private String gender;

	private Long className;

	private String address;
	
	private String parentPhoneNumber;
	
	private String dob;
	
	private String addmissionDate;

}