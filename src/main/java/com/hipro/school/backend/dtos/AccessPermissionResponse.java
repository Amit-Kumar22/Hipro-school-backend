package com.hipro.school.backend.dtos;

import com.hipro.school.backend.enums.AccessAttributeName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccessPermissionResponse {

	private String attributeName;

	private String label;

	private boolean canAdd;

	private boolean canUpdate;

	private boolean canDelete;

	private boolean canRead;

	public static AccessPermissionResponse of(AccessAttributeName attributeName, boolean canAdd, boolean canUpdate,
			boolean canDelete, boolean canRead) {
		return new AccessPermissionResponse(attributeName.name(), attributeName.getLabel(), canAdd, canUpdate, canDelete,
				canRead);
	}

	public static AccessPermissionResponse denied(AccessAttributeName attributeName) {
		return of(attributeName, false, false, false, false);
	}
}
