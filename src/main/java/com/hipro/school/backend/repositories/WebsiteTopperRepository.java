package com.hipro.school.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hipro.school.backend.entities.WebsiteTopper;

@Repository
public interface WebsiteTopperRepository extends JpaRepository<WebsiteTopper, Long> {

}
