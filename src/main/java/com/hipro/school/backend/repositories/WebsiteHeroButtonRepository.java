package com.hipro.school.backend.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hipro.school.backend.entities.WebsiteHeroButton;

@Repository
public interface WebsiteHeroButtonRepository extends JpaRepository<WebsiteHeroButton, Long> {

	Page<WebsiteHeroButton> findAllByHeroSlide_Id(Long heroSlideId, Pageable pageable);

}
