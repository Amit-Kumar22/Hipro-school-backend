package com.hipro.school.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.dtos.WebsiteContactRequest;
import com.hipro.school.backend.entities.WebsiteContact;
import com.hipro.school.backend.exceptions.ResourceNotFoundException;
import com.hipro.school.backend.repositories.WebsiteContactRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WebsiteContactService {

	@Autowired
	private WebsiteContactRepository websiteContactRepository;

	public WebsiteContact create(WebsiteContactRequest request) {
		log.info("Creating website contact: {}", request);
		WebsiteContact websiteContact = mapToEntity(request, new WebsiteContact());
		return websiteContactRepository.save(websiteContact);
	}

	@Transactional
	public WebsiteContact update(Long id, WebsiteContactRequest request) {
		log.info("Updating website contact id: {}", id);
		WebsiteContact existing = getEntity(id);
		mapToEntity(request, existing);
		return websiteContactRepository.save(existing);
	}

	private WebsiteContact mapToEntity(WebsiteContactRequest request, WebsiteContact websiteContact) {
		websiteContact.setPhone(request.getPhone());
		websiteContact.setEmail(request.getEmail());
		websiteContact.setAddressLine1(request.getAddressLine1());
		websiteContact.setAddressLine2(request.getAddressLine2());
		websiteContact.setCity(request.getCity());
		websiteContact.setState(request.getState());
		websiteContact.setPincode(request.getPincode());
		if (request.getCountry() != null) {
			websiteContact.setCountry(request.getCountry());
		}
		websiteContact.setWorkingDays(request.getWorkingDays());
		websiteContact.setWorkingTime(request.getWorkingTime());
		return websiteContact;
	}

	public WebsiteContact getEntity(Long id) {
		log.info("Fetching website contact id: {}", id);
		return websiteContactRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Website contact not found with id: " + id));
	}

	public PageResponse<WebsiteContact> getAll(Pageable pageable) {
		log.info("Fetching all website contacts with pageable: {}", pageable);
		Page<WebsiteContact> page = websiteContactRepository.findAll(pageable);
		return PageResponse.from(page);
	}

	public void delete(Long id) {
		log.info("Deleting website contact id: {}", id);
		WebsiteContact websiteContact = getEntity(id);
		websiteContactRepository.delete(websiteContact);
	}

}
