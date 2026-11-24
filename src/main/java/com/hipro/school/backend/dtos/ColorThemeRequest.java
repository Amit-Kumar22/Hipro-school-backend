package com.hipro.school.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ColorThemeRequest {

	private String themeName;
	
	private String companyName;

	private String primaryColor;

	private String secondaryColor;

	private String backgroundColor;

	private String textColor;

	private String headingColor;

	private String linkColor;

	private String buttonBgColor;

	private String buttonTextColor;

	private String navbarBgColor;

	private String navbarTextColor;

	private String footerBgColor;

	private String footerTextColor;

	private String bottomNavBgColor;

	private String bottomNavTextColor;

}
