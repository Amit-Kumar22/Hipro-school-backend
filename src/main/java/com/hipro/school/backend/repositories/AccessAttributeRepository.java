package com.hipro.school.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hipro.school.backend.entities.AccessAttribute;
import com.hipro.school.backend.enums.AccessAttributeName;

public interface AccessAttributeRepository extends JpaRepository<AccessAttribute, Long> {

	boolean existsByAttributeName(AccessAttributeName attributeName);

	boolean existsByAttributeNameAndIdNot(AccessAttributeName attributeName, Long id);

}
