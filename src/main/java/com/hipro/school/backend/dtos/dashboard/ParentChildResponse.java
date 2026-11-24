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
@Schema(description = "Parent's child basic information")
public class ParentChildResponse {

	@Schema(description = "Student ID")
	private Long studentId;

	@Schema(description = "Student full name")
	private String fullName;

	@Schema(description = "Admission number")
	private String admissionNumber;

	@Schema(description = "Class name")
	private String className;

	@Schema(description = "Roll number")
	private String rollNumber;
}
