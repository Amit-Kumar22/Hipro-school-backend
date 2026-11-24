package com.hipro.school.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hipro.school.backend.entities.AccessPermission;
import com.hipro.school.backend.entities.StaffAccessControl;

public interface AccessPermissionRepository extends JpaRepository<AccessPermission, Integer> {

	List<AccessPermission> findByStaffAccessControl(StaffAccessControl staffAccessControl);

}
