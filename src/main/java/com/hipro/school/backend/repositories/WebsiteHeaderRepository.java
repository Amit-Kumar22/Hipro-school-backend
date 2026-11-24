package com.hipro.school.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hipro.school.backend.entities.WebsiteHeader;

@Repository
public interface WebsiteHeaderRepository extends JpaRepository<WebsiteHeader, Long> {

}
