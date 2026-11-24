package com.hipro.school.backend.dtos;

import com.hipro.school.backend.enums.GalleryType;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebsiteGalleryItemRequest {

	@NotEmpty(message = "Title should not be empty")
	@Size(max = 255, message = "Title must not exceed 255 characters")
	private String title;

	@NotEmpty(message = "Category should not be empty")
	@Size(max = 100, message = "Category must not exceed 100 characters")
	private String category;

	@NotEmpty(message = "Image url should not be empty")
	@Size(max = 500, message = "Image url must not exceed 500 characters")
	private String imageUrl;

	private GalleryType type;

	private Integer displayOrder;

	private Boolean active;
}
