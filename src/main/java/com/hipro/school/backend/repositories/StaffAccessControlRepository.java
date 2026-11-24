package com.hipro.school.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hipro.school.backend.entities.StaffAccessControl;
import com.hipro.school.backend.entities.User;

@Repository
public interface StaffAccessControlRepository extends JpaRepository<StaffAccessControl, Long> {

	Optional<StaffAccessControl> findByStaff(User user);

	Page<StaffAccessControl> findAll(Pageable pageable);
	
	List<StaffAccessControl> findAllByStaffEmail(String email);

}
