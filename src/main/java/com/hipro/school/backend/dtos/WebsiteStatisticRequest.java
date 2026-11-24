package com.hipro.school.backend.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebsiteStatisticRequest {

	@Size(max = 50, message = "Icon must not exceed 50 characters")
	private String icon;

	@NotEmpty(message = "Number should not be empty")
	@Size(max = 50, message = "Number must not exceed 50 characters")
	private String number;

	@NotEmpty(message = "Label should not be empty")
	@Size(max = 150, message = "Label must not exceed 150 characters")
	private String label;

	private Integer displayOrder;

	private Boolean active;
}
