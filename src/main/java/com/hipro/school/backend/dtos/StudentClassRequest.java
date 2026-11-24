package com.hipro.school.backend.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentClassRequest {
	private Long studentId;
	private Long classId;
	private String academicYear;
}