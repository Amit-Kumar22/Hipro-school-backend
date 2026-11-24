package com.hipro.school.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hipro.school.backend.entities.WebsiteFeature;

@Repository
public interface WebsiteFeatureRepository extends JpaRepository<WebsiteFeature, Long> {

}
