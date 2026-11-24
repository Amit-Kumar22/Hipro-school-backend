package com.hipro.school.backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hipro.school.backend.entities.TransportDocumentRule;
import com.hipro.school.backend.enums.VehicleType;

@Repository
public interface TransportDocumentRuleRepository extends JpaRepository<TransportDocumentRule, Long> {

	Optional<TransportDocumentRule> findByVehicleType(VehicleType vehicleType);
}
