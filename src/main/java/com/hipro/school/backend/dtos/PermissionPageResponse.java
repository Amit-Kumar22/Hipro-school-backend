package com.hipro.school.backend.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class PermissionPageResponse<T> extends PageResponse<T> {

	private AccessPermissionResponse accessPermission;

	public static <T> PermissionPageResponse<T> from(PageResponse<T> page, AccessPermissionResponse accessPermission) {
		PermissionPageResponse<T> response = new PermissionPageResponse<>();
		response.setContent(page.getContent());
		response.setTotalElements(page.getTotalElements());
		response.setTotalPages(page.getTotalPages());
		response.setPageNumber(page.getPageNumber());
		response.setPageSize(page.getPageSize());
		response.setLast(page.isLast());
		response.setAccessPermission(accessPermission);
		return response;
	}
}
