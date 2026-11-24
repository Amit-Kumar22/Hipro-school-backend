package com.hipro.school.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hipro.school.backend.entities.WebsiteSetting;

@Repository
public interface WebsiteSettingRepository extends JpaRepository<WebsiteSetting, Long> {

}
