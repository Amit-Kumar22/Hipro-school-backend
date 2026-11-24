package com.hipro.school.backend.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebsiteFeatureRequest {

	@Size(max = 50, message = "Icon must not exceed 50 characters")
	private String icon;

	@NotEmpty(message = "Title should not be empty")
	@Size(max = 150, message = "Title must not exceed 150 characters")
	private String title;

	private String description;

	private Integer displayOrder;

	private Boolean active;
}
