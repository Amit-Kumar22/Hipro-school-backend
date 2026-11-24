package com.hipro.school.backend.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "website_testimonials")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebsiteTestimonial {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private Long id;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String quote;

	@Column(nullable = false, length = 150)
	private String parentName;

	@Column(length = 150)
	private String relation;

	@Column(length = 500)
	private String photoUrl;

	@Column(nullable = false)
	private Integer rating;

	@Column(nullable = false)
	@Builder.Default
	private Integer displayOrder = 0;

	@Column(nullable = false)
	@Builder.Default
	private Boolean active = true;
}
