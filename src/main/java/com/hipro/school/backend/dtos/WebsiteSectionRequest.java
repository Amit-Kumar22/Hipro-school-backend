package com.hipro.school.backend.dtos;

import com.hipro.school.backend.enums.WebsiteSectionType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebsiteSectionRequest {

	@NotNull(message = "Section type is required")
	private WebsiteSectionType sectionType;

	private Boolean enabled;

	private Integer displayOrder;

	@Size(max = 150, message = "Eyebrow must not exceed 150 characters")
	private String eyebrow;

	@Size(max = 255, message = "Title must not exceed 255 characters")
	private String title;

	private String description;
}
