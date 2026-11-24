package com.hipro.school.backend.dtos;

import java.util.List;

import lombok.Data;

@Data
public class StaffPermissionRequest {

	private Long userId;

	private List<PermissionRequest> permissions;

}
