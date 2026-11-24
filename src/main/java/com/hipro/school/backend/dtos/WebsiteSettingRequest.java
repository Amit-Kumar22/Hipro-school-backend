package com.hipro.school.backend.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebsiteSettingRequest {

	@NotEmpty(message = "School name should not be empty")
	@Size(max = 150, message = "School name must not exceed 150 characters")
	private String schoolName;

	@Size(max = 255, message = "Tagline must not exceed 255 characters")
	private String tagline;

	private Integer establishedYear;

	@Size(max = 50, message = "Board must not exceed 50 characters")
	private String board;

	private Boolean active;

	@Size(max = 20, message = "Primary color must not exceed 20 characters")
	private String primaryColor;

	@Size(max = 20, message = "Secondary color must not exceed 20 characters")
	private String secondaryColor;

	@Size(max = 20, message = "Accent color must not exceed 20 characters")
	private String accentColor;

	@Size(max = 20, message = "Text color must not exceed 20 characters")
	private String textColor;

	@Size(max = 20, message = "Light background must not exceed 20 characters")
	private String lightBackground;

	@Size(max = 20, message = "White color must not exceed 20 characters")
	private String whiteColor;
}
