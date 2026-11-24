package com.hipro.school.backend.entities;

import com.hipro.school.backend.enums.HeroButtonStyle;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "website_hero_buttons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebsiteHeroButton {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "hero_slide_id", nullable = false)
	private WebsiteHeroSlide heroSlide;

	@Column(nullable = false, length = 100)
	private String label;

	@Column(nullable = false, length = 500)
	private String url;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private HeroButtonStyle style;

	@Column(length = 50)
	private String icon;

	@Column(nullable = false)
	@Builder.Default
	private Integer displayOrder = 0;

	public Long getHeroSlideId() {
		return heroSlide != null ? heroSlide.getId() : null;
	}

}
