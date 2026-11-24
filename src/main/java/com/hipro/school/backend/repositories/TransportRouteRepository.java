package com.hipro.school.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.hipro.school.backend.entities.TransportRoute;

@Repository
public interface TransportRouteRepository
		extends JpaRepository<TransportRoute, Long>, JpaSpecificationExecutor<TransportRoute> {

	boolean existsByRouteNameIgnoreCase(String routeName);

	boolean existsByRouteNameIgnoreCaseAndIdNot(String routeName, Long id);

	boolean existsByVehicle_IdAndIsActive(Long vehicleId, boolean active);

	boolean existsByDriver_IdAndIsActive(Long driverId, boolean active);

	boolean existsByVehicle_IdAndIsActiveAndIdNot(Long vehicleId, boolean active, Long routeId);

	boolean existsByDriver_IdAndIsActiveAndIdNot(Long driverId, boolean active, Long routeId);

	long countByIsActive(boolean active);

	List<TransportRoute> findByDriver_IdAndIsActive(Long driverId, boolean active);
}
