package com.hipro.school.backend.dtos;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebsiteWhyChoosePointRequest {

	@NotEmpty(message = "Point should not be empty")
	private String point;

	private Integer displayOrder;

	private Boolean active;
}
