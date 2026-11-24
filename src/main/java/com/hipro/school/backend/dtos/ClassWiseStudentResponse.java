package com.hipro.school.backend.dtos;

import java.util.List;

import lombok.Data;

@Data
public class ClassWiseStudentResponse {

	private Long classId;

	private String className;

	private String sectionName;

	private String academicYear;

	private List<CustomerResponse> students;
}