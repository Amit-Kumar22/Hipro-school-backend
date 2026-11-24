package com.hipro.school.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLeadStatsResponse {

	private String userName;
	private String role;
	private int todayLeadCount;
	private int monthlyLeadCount;
	private int totalLeadCount;

}
