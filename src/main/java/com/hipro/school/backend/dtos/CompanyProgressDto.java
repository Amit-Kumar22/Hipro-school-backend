package com.hipro.school.backend.dtos;

import java.util.List;

import lombok.Data;

@Data
public class CompanyProgressDto {

	private Long id;

	private String header;

	private List<String> descriptions;

	private String happyCustomers;

	private String tourCompleted;

	private String tourExperts;

	private String tourDestinations;

	private String targetDetail;
}
