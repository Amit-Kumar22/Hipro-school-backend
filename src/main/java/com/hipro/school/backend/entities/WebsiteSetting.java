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
@Table(name = "website_settings", indexes = { @Index(name = "idx_website_setting_active", columnList = "active") })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebsiteSetting {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private Long id;

	@Column(nullable = false, length = 150)
	private String schoolName;

	@Column(length = 255)
	private String tagline;

	private Integer establishedYear;

	@Column(length = 50)
	private String board;

	@Column(nullable = false)
	@Builder.Default
	private Boolean active = true;

	@Column(length = 20)
	@Builder.Default
	private String primaryColor = "#005C3B";

	@Column(length = 20)
	@Builder.Default
	private String secondaryColor = "#0A6B46";

	@Column(length = 20)
	@Builder.Default
	private String accentColor = "#F4B41A";

	@Column(length = 20)
	@Builder.Default
	private String textColor = "#1A1A1A";

	@Column(length = 20)
	@Builder.Default
	private String lightBackground = "#F8FAF7";

	@Column(length = 20)
	@Builder.Default
	private String whiteColor = "#FFFFFF";
}
