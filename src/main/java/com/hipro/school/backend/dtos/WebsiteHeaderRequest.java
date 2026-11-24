package com.hipro.school.backend.dtos;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebsiteHeaderRequest {

	@Size(max = 500, message = "Logo url must not exceed 500 characters")
	private String logoUrl;

	@Size(max = 255, message = "Logo alt must not exceed 255 characters")
	private String logoAlt;

	private Boolean mobileMenuEnabled;

	@Size(max = 150, message = "Cta label must not exceed 150 characters")
	private String ctaLabel;

	@Size(max = 500, message = "Cta url must not exceed 500 characters")
	private String ctaUrl;
}
