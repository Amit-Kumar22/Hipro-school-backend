package com.hipro.school.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.dtos.WebsiteSettingRequest;
import com.hipro.school.backend.entities.WebsiteSetting;
import com.hipro.school.backend.exceptions.ResourceNotFoundException;
import com.hipro.school.backend.repositories.WebsiteSettingRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WebsiteSettingService {

	@Autowired
	private WebsiteSettingRepository websiteSettingRepository;

	public WebsiteSetting create(WebsiteSettingRequest request) {
		log.info("Creating website setting: {}", request);
		WebsiteSetting websiteSetting = mapToEntity(request, new WebsiteSetting());
		return websiteSettingRepository.save(websiteSetting);
	}

	@Transactional
	public WebsiteSetting update(Long id, WebsiteSettingRequest request) {
		log.info("Updating website setting id: {}", id);
		WebsiteSetting existing = getEntity(id);
		mapToEntity(request, existing);
		return websiteSettingRepository.save(existing);
	}

	private WebsiteSetting mapToEntity(WebsiteSettingRequest request, WebsiteSetting websiteSetting) {
		if (request.getSchoolName() != null) {
			websiteSetting.setSchoolName(request.getSchoolName());
		}
		if (request.getTagline() != null) {
			websiteSetting.setTagline(request.getTagline());
		}
		if (request.getEstablishedYear() != null) {
			websiteSetting.setEstablishedYear(request.getEstablishedYear());
		}
		if (request.getBoard() != null) {
			websiteSetting.setBoard(request.getBoard());
		}
		if (request.getActive() != null) {
			websiteSetting.setActive(request.getActive());
		}
		if (request.getPrimaryColor() != null) {
			websiteSetting.setPrimaryColor(request.getPrimaryColor());
		}
		if (request.getSecondaryColor() != null) {
			websiteSetting.setSecondaryColor(request.getSecondaryColor());
		}
		if (request.getAccentColor() != null) {
			websiteSetting.setAccentColor(request.getAccentColor());
		}
		if (request.getTextColor() != null) {
			websiteSetting.setTextColor(request.getTextColor());
		}
		if (request.getLightBackground() != null) {
			websiteSetting.setLightBackground(request.getLightBackground());
		}
		if (request.getWhiteColor() != null) {
			websiteSetting.setWhiteColor(request.getWhiteColor());
		}
		return websiteSetting;
	}

	public WebsiteSetting getEntity(Long id) {
		log.info("Fetching website setting id: {}", id);
		return websiteSettingRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("Website setting not found with id: " + id));
	}

	public PageResponse<WebsiteSetting> getAll(Pageable pageable) {
		log.info("Fetching all website settings with pageable: {}", pageable);
		Page<WebsiteSetting> page = websiteSettingRepository.findAll(pageable);
		return PageResponse.from(page);
	}

	public void delete(Long id) {
		log.info("Deleting website setting id: {}", id);
		WebsiteSetting websiteSetting = getEntity(id);
		websiteSettingRepository.delete(websiteSetting);
	}

}
