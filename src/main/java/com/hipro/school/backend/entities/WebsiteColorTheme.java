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
@Table(name = "color_theme", indexes = { @Index(name = "idx_color_theme_active", columnList = "is_active") })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebsiteColorTheme {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private Long id;

	@Column(name = "theme_name", length = 100, nullable = false)
	private String themeName;
	
	@Column(name = "theme_type", length = 50)
	private String themeType;
	
	@Column(name = "company_name", length = 80, nullable = false)
	private String companyName;

	@Column(name = "primary_color", length = 30, nullable = false)
	private String primaryColor;

	@Column(name = "secondary_color", length = 30)
	private String secondaryColor;

	@Column(name = "background_color", length = 30, nullable = false)
	private String backgroundColor;

	@Column(name = "text_color", length = 30, nullable = false)
	private String textColor;

	@Column(name = "heading_color", length = 30)
	private String headingColor;

	@Column(name = "link_color", length = 30)
	private String linkColor;

	@Column(name = "button_bg_color", length = 30)
	private String buttonBgColor;

	@Column(name = "button_text_color", length = 30)
	private String buttonTextColor;

	@Column(name = "navbar_bg_color", length = 30)
	private String navbarBgColor;

	@Column(name = "navbar_text_color", length = 30)
	private String navbarTextColor;

	@Column(name = "footer_bg_color", length = 30)
	private String footerBgColor;

	@Column(name = "footer_text_color", length = 30)
	private String footerTextColor;

	@Column(name = "is_active")
	private Boolean isActive;
	
	@Column(name = "bottom_nav_color", length = 30)
	private String bottomNavBgColor;

	@Column(name = "bottom_nav_text_color", length = 30)
	private String bottomNavTextColor;

}
