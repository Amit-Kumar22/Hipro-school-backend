package com.hipro.school.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CountryResponse {
	
	private Long countryId;

	private String countryName;
	
	 private String countryCode;

}
