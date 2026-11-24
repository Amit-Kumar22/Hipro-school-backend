package com.hipro.school.backend.dtos;

import lombok.Data;

@Data
public class PermissionRequest {

	private Long attributeId;
	private boolean canAdd;
	private boolean canUpdate;
	private boolean canDelete;
	private boolean canRead;

}
