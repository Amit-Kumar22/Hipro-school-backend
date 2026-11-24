package com.hipro.school.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.hipro.school.backend.entities.StudentConcession;

public interface StudentConcessionRepository
		extends JpaRepository<StudentConcession, Long>, JpaSpecificationExecutor<StudentConcession> {

}