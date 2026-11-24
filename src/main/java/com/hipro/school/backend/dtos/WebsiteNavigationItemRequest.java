package com.hipro.school.backend.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebsiteNavigationItemRequest {

	@NotEmpty(message = "Label should not be empty")
	@Size(max = 100, message = "Label must not exceed 100 characters")
	private String label;

	@NotEmpty(message = "Url should not be empty")
	@Size(max = 500, message = "Url must not exceed 500 characters")
	private String url;

	private Integer displayOrder;

	private Boolean active;
}
