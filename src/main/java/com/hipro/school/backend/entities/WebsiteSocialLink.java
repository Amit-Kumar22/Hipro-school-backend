package com.hipro.school.backend.entities;

import com.hipro.school.backend.enums.SocialPlatform;

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
@Table(name = "website_social_links", indexes = { @Index(name = "idx_social_platform", columnList = "platform") })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebsiteSocialLink {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private SocialPlatform platform;

	@Column(nullable = false, length = 500)
	private String url;

	@Column(nullable = false)
	@Builder.Default
	private Boolean active = true;

	@Column(nullable = false)
	@Builder.Default
	private Integer displayOrder = 0;
}
