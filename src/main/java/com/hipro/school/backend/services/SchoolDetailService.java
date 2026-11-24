package com.hipro.school.backend.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.dtos.SchoolCordinateRequest;
import com.hipro.school.backend.dtos.SchoolRequest;
import com.hipro.school.backend.entities.School;
import com.hipro.school.backend.entities.User;
import com.hipro.school.backend.exceptions.ResourceNotFoundException;
import com.hipro.school.backend.repositories.SchoolRepository;
import com.hipro.school.backend.repositories.UserRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SchoolDetailService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private SchoolRepository schoolRepository;

	@Autowired
	private ModelMapper modelMapper;

	@Transactional
	public PageResponse<School> getSchoolDetails(Pageable pageable) {
		log.info(">> getSchoolDetails({})", pageable);
		Page<School> schools = schoolRepository.findAll(pageable);
		return PageResponse.from(schools);
	}

	public School getSchoolDetail(String email) {
		log.info(">> getSchoolDetailById({})", email);
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("Admin Detail not found."));
		return null;
	}

	public School getSchoolDetailById(Long id) {
		log.info(">> getSchoolDetailById({})", id);
		return schoolRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("School not found with id: " + id));
	}

	@Transactional
	public School createSchool(SchoolRequest request, String email) {
		log.info(">> createSchool({})", email);
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("Admin Detail not found."));
		School school = schoolRepository.findByUser_Id(user.getId()).orElseGet(School::new);
		boolean isNew = school.getId() == null;
		mapToEntity(request, school, user);
		School saved = schoolRepository.save(school);
		log.info("School details {} successfully for admin {}", isNew ? "created" : "updated", email);
		return saved;
	}

	private void mapToEntity(SchoolRequest request, School school, User user) {
		school.setUser(user);
		if (school.getSchoolCode() == null) {
			school.setSchoolCode(request.getSchoolCode());
		}
		school.setSchoolName(request.getSchoolName());
		school.setRegistrationNumber(request.getRegistrationNumber());
		school.setAffiliationBoard(request.getAffiliationBoard());
		school.setEstablishedYear(request.getEstablishedYear());
		school.setEmail(request.getEmail());
		school.setPhoneNumber(request.getPhoneNumber());
		school.setAlternatePhone(request.getAlternatePhone());
		school.setWebsite(request.getWebsite());
		school.setLogoUrl(request.getLogoUrl());
		school.setPrincipalName(request.getPrincipalName());
		school.setTotalStudents(request.getTotalStudents());
		school.setTotalTeachers(request.getTotalTeachers());
		if (request.getAddress() != null) {
			school.setAddress(request.getAddress());
		}
	}

	@Transactional
	public School updateSchool(Long id, SchoolRequest request) {
		log.info(">> updateSchool({})", id);
		School school = schoolRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("School not found with id: " + id));
		modelMapper.map(request, school);
		return schoolRepository.save(school);
	}

	@Transactional
	public void updateSchoolCordinate(Long id, SchoolCordinateRequest request) {
		log.info(">> updateSchool({})", id);
		School school = schoolRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("School not found with id: " + id));
		school.setLatitude(request.getLatitude());
		school.setLongitude(request.getLongitude());
		school.setAllowedRadiusMeters(request.getAllowedRadiusMeters());
		schoolRepository.save(school);
	}

	@Transactional
	public void deleteSchool(Long id) {
		log.info(">> deleteSchool({})", id);
		School school = schoolRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("School not found with id: " + id));
		schoolRepository.delete(school);
	}

}