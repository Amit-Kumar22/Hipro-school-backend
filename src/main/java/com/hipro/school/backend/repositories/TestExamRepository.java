package com.hipro.school.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.hipro.school.backend.entities.TestExam;

@Repository
public interface TestExamRepository extends JpaRepository<TestExam, Long>, JpaSpecificationExecutor<TestExam> {

	

}
