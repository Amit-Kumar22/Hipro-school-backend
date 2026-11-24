package com.hipro.school.backend.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebsiteTestimonialRequest {

	@NotEmpty(message = "Quote should not be empty")
	private String quote;

	@NotEmpty(message = "Parent name should not be empty")
	@Size(max = 150, message = "Parent name must not exceed 150 characters")
	private String parentName;

	@Size(max = 150, message = "Relation must not exceed 150 characters")
	private String relation;

	@Size(max = 500, message = "Photo url must not exceed 500 characters")
	private String photoUrl;

	@NotNull(message = "Rating is required")
	@Min(value = 1, message = "Rating must be at least 1")
	@Max(value = 5, message = "Rating must not exceed 5")
	private Integer rating;

	private Integer displayOrder;

	private Boolean active;
}
