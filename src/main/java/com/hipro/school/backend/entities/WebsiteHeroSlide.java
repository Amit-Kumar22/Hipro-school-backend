package com.hipro.school.backend.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "website_hero_slides", indexes = { @Index(name = "idx_hero_order", columnList = "display_order") })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebsiteHeroSlide {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private Long id;

	@Column(length = 500)
	private String backgroundImageUrl;

	@Column(length = 100)
	private String badge;

	@Column(nullable = false, length = 255)
	private String title;

	@Column(length = 255)
	private String subtitle;

	@Column(columnDefinition = "TEXT")
	private String description;

	@Column(nullable = false)
	@Builder.Default
	private Integer displayOrder = 0;

	@Column(nullable = false)
	@Builder.Default
	private Boolean active = true;
}
