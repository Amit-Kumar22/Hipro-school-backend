package com.hipro.school.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hipro.school.backend.entities.SchoolLead;

@Repository
public interface SchoolLeadRepository extends JpaRepository<SchoolLead, Long> {

	boolean existsByPhoneAndSchoolNameAndCity(String phone, String schoolName, String city);
	
	boolean existsByPhone(String phone);
}