package com.hipro.school.backend.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebsiteHeroSlideRequest {

	@Size(max = 500, message = "Background image url must not exceed 500 characters")
	private String backgroundImageUrl;

	@Size(max = 100, message = "Badge must not exceed 100 characters")
	private String badge;

	@NotEmpty(message = "Title should not be empty")
	@Size(max = 255, message = "Title must not exceed 255 characters")
	private String title;

	@Size(max = 255, message = "Subtitle must not exceed 255 characters")
	private String subtitle;

	private String description;

	private Integer displayOrder;

	private Boolean active;
}
