package com.hipro.school.backend.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SectionRequest {

	@NotBlank
	private String sectionName;

	private Long classId;

}