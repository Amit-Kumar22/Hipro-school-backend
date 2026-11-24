package com.hipro.school.backend.dtos;

import java.util.List;

import com.hipro.school.backend.entities.SchoolClass;

import lombok.Data;

@Data
public class SectionResponse {

	private SchoolClass schoolClass;

	private List<SectionDTO> sections;

	@Data
	public static class SectionDTO {
		private Long id;
		private String sectionName;
	}
}