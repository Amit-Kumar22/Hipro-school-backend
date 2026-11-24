package com.hipro.school.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.hipro.school.backend.entities.StudentTransportAssignment;

@Repository
public interface StudentTransportAssignmentRepository
		extends JpaRepository<StudentTransportAssignment, Long>, JpaSpecificationExecutor<StudentTransportAssignment> {

	Optional<StudentTransportAssignment> findByStudent_IdAndIsActive(Long studentId, boolean active);

	boolean existsByStudent_IdAndIsActive(Long studentId, boolean active);

	long countByRoute_IdAndIsActive(Long routeId, boolean active);

	long countByIsActive(boolean active);

	List<StudentTransportAssignment> findByRoute_IdAndIsActive(Long routeId, boolean active);
}
