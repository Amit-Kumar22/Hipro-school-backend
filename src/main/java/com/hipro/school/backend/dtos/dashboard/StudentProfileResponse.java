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
@Schema(description = "Student profile information for dashboard")
public class StudentProfileResponse {

	@Schema(description = "Student ID")
	private Long studentId;

	@Schema(description = "Student full name")
	private String fullName;

	@Schema(description = "Admission number")
	private String admissionNumber;

	@Schema(description = "Roll number")
	private String rollNumber;

	@Schema(description = "Class name")
	private String className;

	@Schema(description = "Academic year")
	private String academicYear;

	@Schema(description = "Date of birth")
	private String dateOfBirth;

	@Schema(description = "Gender")
	private String gender;

	@Schema(description = "Father name")
	private String fatherName;

	@Schema(description = "Mother name")
	private String motherName;

	@Schema(description = "Parent email")
	private String parentEmail;

	@Schema(description = "Student email")
	private String studentEmail;

	@Schema(description = "Phone number")
	private String phone;
}
