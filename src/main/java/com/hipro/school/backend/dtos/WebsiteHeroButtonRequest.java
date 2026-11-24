package com.hipro.school.backend.dtos;

import com.hipro.school.backend.enums.HeroButtonStyle;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebsiteHeroButtonRequest {

	@NotNull(message = "Hero slide id is required")
	private Long heroSlideId;

	@NotEmpty(message = "Label should not be empty")
	@Size(max = 100, message = "Label must not exceed 100 characters")
	private String label;

	@NotEmpty(message = "Url should not be empty")
	@Size(max = 500, message = "Url must not exceed 500 characters")
	private String url;

	private HeroButtonStyle style;

	@Size(max = 50, message = "Icon must not exceed 50 characters")
	private String icon;

	private Integer displayOrder;
}
