package com.hipro.school.backend.dtos;

import java.util.List;

import lombok.Data;

@Data
public class CreateGroupRequest {
	
	private String schoolCode;

	private String name;

	//private Integer adminId;

	private List<Long> userIds;
}