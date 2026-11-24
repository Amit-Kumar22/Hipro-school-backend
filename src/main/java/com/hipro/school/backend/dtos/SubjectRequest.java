package com.hipro.school.backend.dtos;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class SubjectRequest {

	@NotEmpty(message = "Subject name should not be empty")
	private String subjectName;
	
	private String subjectCode;
	

}