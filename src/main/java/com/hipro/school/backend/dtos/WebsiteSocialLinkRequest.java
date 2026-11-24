package com.hipro.school.backend.dtos;

import com.hipro.school.backend.enums.SocialPlatform;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebsiteSocialLinkRequest {

	@NotNull(message = "Platform is required")
	private SocialPlatform platform;

	@NotEmpty(message = "Url should not be empty")
	@Size(max = 500, message = "Url must not exceed 500 characters")
	private String url;

	private Boolean active;

	private Integer displayOrder;
}
