package com.hipro.school.backend.dtos;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebsiteTopperRequest {

	@NotNull(message = "Rank is required")
	private Integer rank;

	@NotEmpty(message = "Name should not be empty")
	@Size(max = 150, message = "Name must not exceed 150 characters")
	private String name;

	@Size(max = 500, message = "Photo url must not exceed 500 characters")
	private String photoUrl;

	@NotNull(message = "Percentage is required")
	@DecimalMin(value = "0.0", message = "Percentage must be at least 0")
	@DecimalMax(value = "100.0", message = "Percentage must not exceed 100")
	private BigDecimal percentage;

	@Size(max = 100, message = "Stream must not exceed 100 characters")
	private String stream;

	@NotEmpty(message = "Session should not be empty")
	@Size(max = 30, message = "Session must not exceed 30 characters")
	private String session;

	private Boolean active;
}
