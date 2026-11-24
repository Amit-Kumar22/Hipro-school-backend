package com.hipro.school.backend.entities;

import static com.hipro.school.backend.enums.Permission.ADMIN_CREATE;
import static com.hipro.school.backend.enums.Permission.DRIVER_CREATE;
import static com.hipro.school.backend.enums.Permission.DRIVER_DELETE;
import static com.hipro.school.backend.enums.Permission.DRIVER_READ;
import static com.hipro.school.backend.enums.Permission.DRIVER_UPDATE;
import static com.hipro.school.backend.enums.Permission.ADMIN_DELETE;
import static com.hipro.school.backend.enums.Permission.ADMIN_READ;
import static com.hipro.school.backend.enums.Permission.ADMIN_UPDATE;
import static com.hipro.school.backend.enums.Permission.PARENT_CREATE;
import static com.hipro.school.backend.enums.Permission.PARENT_DELETE;
import static com.hipro.school.backend.enums.Permission.PARENT_READ;
import static com.hipro.school.backend.enums.Permission.PARENT_UPDATE;
import static com.hipro.school.backend.enums.Permission.PRINCIPAL_CREATE;
import static com.hipro.school.backend.enums.Permission.PRINCIPAL_DELETE;
import static com.hipro.school.backend.enums.Permission.PRINCIPAL_READ;
import static com.hipro.school.backend.enums.Permission.PRINCIPAL_UPDATE;
import static com.hipro.school.backend.enums.Permission.STAFF_CREATE;
import static com.hipro.school.backend.enums.Permission.STAFF_DELETE;
import static com.hipro.school.backend.enums.Permission.STAFF_READ;
import static com.hipro.school.backend.enums.Permission.STAFF_UPDATE;
import static com.hipro.school.backend.enums.Permission.STUDENT_CREATE;
import static com.hipro.school.backend.enums.Permission.STUDENT_DELETE;
import static com.hipro.school.backend.enums.Permission.STUDENT_READ;
import static com.hipro.school.backend.enums.Permission.STUDENT_UPDATE;
import static com.hipro.school.backend.enums.Permission.SUPERADMIN_CREATE;
import static com.hipro.school.backend.enums.Permission.SUPERADMIN_DELETE;
import static com.hipro.school.backend.enums.Permission.SUPERADMIN_READ;
import static com.hipro.school.backend.enums.Permission.SUPERADMIN_UPDATE;
import static com.hipro.school.backend.enums.Permission.TEACHER_CREATE;
import static com.hipro.school.backend.enums.Permission.TEACHER_DELETE;
import static com.hipro.school.backend.enums.Permission.TEACHER_READ;
import static com.hipro.school.backend.enums.Permission.TEACHER_UPDATE;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.hipro.school.backend.enums.Permission;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Role {

	SUPERADMIN(Set.of(ADMIN_READ, ADMIN_UPDATE, ADMIN_DELETE, ADMIN_CREATE, SUPERADMIN_CREATE, SUPERADMIN_DELETE,
			SUPERADMIN_READ, SUPERADMIN_UPDATE)),

	ADMIN(Set.of(ADMIN_READ, ADMIN_UPDATE, ADMIN_DELETE, ADMIN_CREATE)),

	STUDENT(Set.of(STUDENT_READ, STUDENT_UPDATE, STUDENT_DELETE, STUDENT_CREATE)),

	TEACHER(Set.of(TEACHER_READ, TEACHER_UPDATE, TEACHER_DELETE, TEACHER_CREATE)),

	PRINCIPAL(Set.of(PRINCIPAL_READ, PRINCIPAL_UPDATE, PRINCIPAL_DELETE, PRINCIPAL_CREATE)),
	
	PARENT(Set.of(PARENT_READ, PARENT_UPDATE, PARENT_DELETE, PARENT_CREATE)),
	
	STAFF(Set.of(STAFF_READ, STAFF_UPDATE, STAFF_DELETE, STAFF_CREATE)),

	DRIVER(Set.of(DRIVER_READ, DRIVER_UPDATE, DRIVER_DELETE, DRIVER_CREATE));


	@Getter
	private final Set<Permission> permissions;

	public List<SimpleGrantedAuthority> getAuthorities() {
		var authorities = getPermissions().stream()
				.map(permission -> new SimpleGrantedAuthority(permission.getPermission())).collect(Collectors.toList());
		authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
		return authorities;
	}
}
