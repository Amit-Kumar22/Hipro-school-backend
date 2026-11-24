package com.hipro.school.backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.hipro.school.backend.entities.StaffSalaryTemplate;

@Repository
public interface StaffSalaryTemplateRepository
		extends JpaRepository<StaffSalaryTemplate, Long>, JpaSpecificationExecutor<StaffSalaryTemplate> {

	Optional<StaffSalaryTemplate> findByActiveTrue();

	boolean existsByActiveTrue();

	long count();
}
