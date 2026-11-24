package com.hipro.school.backend.entities;

import com.hipro.school.backend.enums.WebsiteSectionType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "website_sections", indexes = { @Index(name = "idx_section_type", columnList = "section_type") })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebsiteSection {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(name = "section_type", nullable = false, length = 50)
	private WebsiteSectionType sectionType;

	@Column(nullable = false)
	@Builder.Default
	private Boolean enabled = true;

	@Column(nullable = false)
	@Builder.Default
	private Integer displayOrder = 0;

	@Column(length = 150)
	private String eyebrow;

	@Column(length = 255)
	private String title;

	@Column(columnDefinition = "TEXT")
	private String description;
}
