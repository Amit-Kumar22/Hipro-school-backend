package com.hipro.school.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.dtos.WebsiteFloatingSettingRequest;
import com.hipro.school.backend.entities.WebsiteFloatingSetting;
import com.hipro.school.backend.exceptions.ResourceNotFoundException;
import com.hipro.school.backend.repositories.WebsiteFloatingSettingRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WebsiteFloatingSettingService {

	@Autowired
	private WebsiteFloatingSettingRepository websiteFloatingSettingRepository;

	public WebsiteFloatingSetting create(WebsiteFloatingSettingRequest request) {
		log.info("Creating website floating setting: {}", request);
		WebsiteFloatingSetting setting = mapToEntity(request, new WebsiteFloatingSetting());
		return websiteFloatingSettingRepository.save(setting);
	}

	@Transactional
	public WebsiteFloatingSetting update(Long id, WebsiteFloatingSettingRequest request) {
		log.info("Updating website floating setting id: {}", id);
		WebsiteFloatingSetting existing = getEntity(id);
		mapToEntity(request, existing);
		return websiteFloatingSettingRepository.save(existing);
	}

	private WebsiteFloatingSetting mapToEntity(WebsiteFloatingSettingRequest request,
			WebsiteFloatingSetting websiteFloatingSetting) {
		if (request.getBackToTopEnabled() != null) {
			websiteFloatingSetting.setBackToTopEnabled(request.getBackToTopEnabled());
		}
		if (request.getWhatsappEnabled() != null) {
			websiteFloatingSetting.setWhatsappEnabled(request.getWhatsappEnabled());
		}
		websiteFloatingSetting.setWhatsappPhone(request.getWhatsappPhone());
		websiteFloatingSetting.setWhatsappMessage(request.getWhatsappMessage());
		if (request.getQuickEnquiryEnabled() != null) {
			websiteFloatingSetting.setQuickEnquiryEnabled(request.getQuickEnquiryEnabled());
		}
		websiteFloatingSetting.setQuickEnquiryLabel(request.getQuickEnquiryLabel());
		websiteFloatingSetting.setQuickEnquiryUrl(request.getQuickEnquiryUrl());
		return websiteFloatingSetting;
	}

	public WebsiteFloatingSetting getEntity(Long id) {
		log.info("Fetching website floating setting id: {}", id);
		return websiteFloatingSettingRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("Website floating setting not found with id: " + id));
	}

	public PageResponse<WebsiteFloatingSetting> getAll(Pageable pageable) {
		log.info("Fetching all website floating settings with pageable: {}", pageable);
		Page<WebsiteFloatingSetting> page = websiteFloatingSettingRepository.findAll(pageable);
		return PageResponse.from(page);
	}

	public void delete(Long id) {
		log.info("Deleting website floating setting id: {}", id);
		WebsiteFloatingSetting websiteFloatingSetting = getEntity(id);
		websiteFloatingSettingRepository.delete(websiteFloatingSetting);
	}

}
