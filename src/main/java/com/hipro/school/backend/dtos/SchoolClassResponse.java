package com.hipro.school.backend.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SchoolClassResponse {

	private Long id;

	private String className;

	private String description;

	private Boolean active;

	private UserResponse classTeacher;

}
