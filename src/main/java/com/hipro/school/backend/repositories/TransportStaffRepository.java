package com.hipro.school.backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.hipro.school.backend.entities.TransportStaff;
import com.hipro.school.backend.enums.TransportEmploymentStatus;

@Repository
public interface TransportStaffRepository
		extends JpaRepository<TransportStaff, Long>, JpaSpecificationExecutor<TransportStaff> {

	long countByEmploymentStatusAndIsActive(TransportEmploymentStatus status, boolean active);

	long countByEmploymentStatus(TransportEmploymentStatus status);

	Optional<TransportStaff> findByUser_Id(Long userId);
}
