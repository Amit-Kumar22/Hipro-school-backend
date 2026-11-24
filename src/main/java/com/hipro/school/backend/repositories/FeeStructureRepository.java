package com.hipro.school.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.hipro.school.backend.entities.FeeStructure;
import com.hipro.school.backend.enums.FeeType;

@Repository
public interface FeeStructureRepository
		extends JpaRepository<FeeStructure, Long>, JpaSpecificationExecutor<FeeStructure> {

	boolean existsByClassRoom_IdAndFeeType(Long classId, FeeType feeType);

	List<FeeStructure> findAllByClassRoom_Id(Long classId);
}
