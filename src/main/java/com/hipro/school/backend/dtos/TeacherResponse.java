package com.hipro.school.backend.dtos;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hipro.school.backend.entities.Teacher;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TeacherResponse {

	private Long id;

	private String fullName;

	private String email;

	private String phone;

	private String employeeCode;

	private String qualification;

	private BigDecimal experienceYears;

	private Long subjectId;

	private String subjectName;

	public static TeacherResponse from(Teacher teacher) {
		return TeacherResponse.builder().id(teacher.getId())
				.fullName(teacher.getTeacherUser() != null ? teacher.getTeacherUser().getFullName() : null)
				.email(teacher.getTeacherUser() != null ? teacher.getTeacherUser().getEmail() : null)
				.phone(teacher.getTeacherUser() != null ? teacher.getTeacherUser().getPhone() : null)
				.employeeCode(teacher.getEmployeeCode()).qualification(teacher.getQualification())
				.experienceYears(teacher.getExperienceYears())
				.subjectId(teacher.getSubject() != null ? teacher.getSubject().getId() : null)
				.subjectName(teacher.getSubject() != null ? teacher.getSubject().getSubjectName() : null).build();
	}

}
