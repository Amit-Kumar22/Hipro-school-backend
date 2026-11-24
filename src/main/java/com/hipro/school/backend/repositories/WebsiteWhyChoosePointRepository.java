package com.hipro.school.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hipro.school.backend.entities.WebsiteWhyChoosePoint;

@Repository
public interface WebsiteWhyChoosePointRepository extends JpaRepository<WebsiteWhyChoosePoint, Long> {

}
