package com.hipro.school.backend.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "about_us")
public class AboutUs extends BaseEntity {

	private String imageLink;

	@NotBlank(message = "PathFinder description is required")
	@Size(max = 500)
	@Column(name = "pathfinder_description", length = 500)
	private String description;

}