package com.hipro.school.backend.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hipro.school.backend.entities.TransportVehicle;

@Repository
public interface TransportVehicleRepository
		extends JpaRepository<TransportVehicle, Long>, JpaSpecificationExecutor<TransportVehicle> {

	boolean existsByRegistrationNumberIgnoreCase(String registrationNumber);

	boolean existsByRegistrationNumberIgnoreCaseAndIdNot(String registrationNumber, Long id);

	long countByIsActive(boolean active);

	@Query("""
			SELECT v FROM TransportVehicle v WHERE v.isActive = true
			AND (v.insuranceExpiry <= :cutoff OR v.fitnessExpiry <= :cutoff
				OR v.permitExpiry <= :cutoff OR v.pucExpiry <= :cutoff)""")
	List<TransportVehicle> findWithUpcomingDocumentExpiry(@Param("cutoff") LocalDate cutoff);
}
