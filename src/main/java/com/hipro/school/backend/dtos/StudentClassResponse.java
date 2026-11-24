package com.hipro.school.backend.dtos;

import com.hipro.school.backend.entities.SchoolClass;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentClassResponse {

	private CustomerResponse student;

	private SchoolClass schoolClass;

	private String academicYear;
}