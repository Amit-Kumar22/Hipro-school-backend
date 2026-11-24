package com.hipro.school.backend.dtos;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListFeaturedCityResponse {

	private String title;

	private List<CityResponse> cities;



}
