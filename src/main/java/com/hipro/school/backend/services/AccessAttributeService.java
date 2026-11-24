package com.hipro.school.backend.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hipro.school.backend.entities.AccessAttribute;
import com.hipro.school.backend.exceptions.ServiceException;
import com.hipro.school.backend.repositories.AccessAttributeRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class AccessAttributeService {

	@Autowired
	private AccessAttributeRepository accessAttributeRepository;

	public List<AccessAttribute> getAllAttributes() {
		log.info(">> getAllAttributes()");
		return accessAttributeRepository.findAll();
	}

	public AccessAttribute getAttributeById(Long id) {
		log.info(">> getAttributeById({})", id);
		return accessAttributeRepository.findById(id)
				.orElseThrow(() -> new ServiceException("AccessAttribute not found with id: " + id));
	}

	public void addAttribute(AccessAttribute req) {
		log.info(">> addAttribute({})", req);

		if (req.getAttributeName() == null) {
			throw new ServiceException("attributeName is required");
		}

		// Prevent duplicate attribute names
		boolean exists = accessAttributeRepository.existsByAttributeName(req.getAttributeName());
		if (exists) {
			throw new ServiceException("AccessAttribute already exists with name: " + req.getAttributeName());
		}
		accessAttributeRepository.save(req);
	}

	public void updateAttribute(Long id, AccessAttribute req) {
		log.info(">> updateAttribute({}, {})", id, req);

		AccessAttribute existing = getAttributeById(id);

		if (req.getAttributeName() == null) {
			throw new ServiceException("attributeName is required");
		}

		// Duplicate check on update
		boolean exists = accessAttributeRepository.existsByAttributeNameAndIdNot(req.getAttributeName(), id);

		if (exists) {
			throw new ServiceException("AccessAttribute already exists with name: " + req.getAttributeName());
		}
		existing.setAttributeName(req.getAttributeName());
		accessAttributeRepository.save(existing);
	}

	public void deleteAttribute(Long id) {
		log.info(">> deleteAttribute({})", id);

		try {
			AccessAttribute existing = getAttributeById(id);
			accessAttributeRepository.delete(existing);
		} catch (DataIntegrityViolationException e) {
			throw new ServiceException("Cannot delete AccessAttribute. It is already used in permissions.");
		}
	}
}
