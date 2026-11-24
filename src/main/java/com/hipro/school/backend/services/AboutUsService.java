package com.hipro.school.backend.services;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.entities.AboutUs;
import com.hipro.school.backend.repositories.AboutUsRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AboutUsService {

	@Autowired
	private AboutUsRepository aboutUsRepository;

	public AboutUs createAboutUs(AboutUs aboutUs) {
		log.info("Creating AboutUs section");
		return aboutUsRepository.save(aboutUs);
	}

	public AboutUs updateAboutUs(Long id, AboutUs aboutUs) {
		log.info("Updating AboutUs id: {}", id);
		AboutUs existing = aboutUsRepository.findById(id).orElseThrow(() -> new RuntimeException("AboutUs not found"));
		BeanUtils.copyProperties(aboutUs, existing, "id", "createdAt");
		return aboutUsRepository.save(existing);
	}

	public AboutUs getAboutUs(Long id) {
		log.info("Fetching AboutUs id: {}", id);
		return aboutUsRepository.findById(id).orElseThrow(() -> new RuntimeException("AboutUs not found"));
	}

	public PageResponse<AboutUs> getAllAboutUs(Pageable pageable) {
		log.info("Fetching all AboutUs records");
		Page<AboutUs> pages = aboutUsRepository.findAll(pageable);
		return PageResponse.from(pages);
	}

	public void deleteAboutUs(Long id) {
		log.info("Deleting AboutUs id: {}", id);
		AboutUs aboutUs = aboutUsRepository.findById(id).orElseThrow(() -> new RuntimeException("AboutUs not found"));
		aboutUsRepository.delete(aboutUs);
	}
}