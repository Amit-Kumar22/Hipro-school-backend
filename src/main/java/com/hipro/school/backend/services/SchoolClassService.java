package com.hipro.school.backend.services;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.hipro.school.backend.dtos.ClassRequest;
import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.dtos.SchoolClassResponse;
import com.hipro.school.backend.dtos.UserResponse;
import com.hipro.school.backend.entities.SchoolClass;
import com.hipro.school.backend.entities.User;
import com.hipro.school.backend.exceptions.ResourceNotFoundException;
import com.hipro.school.backend.exceptions.ServiceException;
import com.hipro.school.backend.repositories.SchoolClassRepository;
import com.hipro.school.backend.repositories.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SchoolClassService {

	@Autowired
	private SchoolClassRepository schoolClassRepository;

	@Autowired
	private UserRepository userRepository;

	public PageResponse<SchoolClassResponse> getAllClasses(Pageable pageable) {
		log.info("Fetching all school classes");
		Page<SchoolClass> page = schoolClassRepository.findAll(pageable);
		return PageResponse.from(page.map(this::toResponse));
	}

	public SchoolClassResponse getClassById(Long id) {
		log.info("Fetching school class for id: {}", id);
		SchoolClass schoolClass = schoolClassRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("School class not found with id : " + id));
		return toResponse(schoolClass);
	}

	@Transactional
	public void saveClass(ClassRequest request) {
		log.info("Creating school classes");

		if (!StringUtils.hasText(request.getClassName())) {
			throw new ServiceException("Class name is required");
		}

		List<SchoolClass> classes = Arrays.stream(request.getClassName().split(",")).map(String::trim)
				.filter(StringUtils::hasText).distinct().map(className -> {
					SchoolClass schoolClass = new SchoolClass();
					schoolClass.setClassName(className);
					return schoolClass;
				}).toList();

		if (classes.isEmpty()) {
			throw new ServiceException("At least one valid class name is required");
		}
		schoolClassRepository.saveAll(classes);
		log.info("Created {} classes", classes.size());
	}

	public void updateClass(Long id, SchoolClass request) {
		log.info("Updating school class id: {}", id);
		SchoolClass schoolClass = schoolClassRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("School class not found with id : " + id));
		schoolClass.setClassName(request.getClassName());
		schoolClass.setDescription(request.getDescription());
		schoolClassRepository.save(schoolClass);
	}

	public void assignTeacher(Long classId, Long teacherId) {
		log.info("Assigning teacher id: {} to class id: {}", teacherId, classId);
		SchoolClass schoolClass = schoolClassRepository.findById(classId)
				.orElseThrow(() -> new ResourceNotFoundException("School class not found with id : " + classId));
		User teacher = userRepository.findById(teacherId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with id : " + teacherId));

		schoolClassRepository.findByUserId(teacherId).ifPresent(existingClass -> {
			throw new ServiceException(
					"Teacher is already assigned as class teacher to class: " + existingClass.getClassName());
		});

		schoolClass.setUser(teacher);
		schoolClassRepository.save(schoolClass);
	}

	public void deleteClass(Long id) {
		log.info("Deleting school class id: {}", id);
		SchoolClass schoolClass = schoolClassRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("School class not found with id : " + id));
		schoolClassRepository.save(schoolClass);
	}

	private SchoolClassResponse toResponse(SchoolClass schoolClass) {
		UserResponse teacherResponse = null;
		if (schoolClass.getUser() != null) {
			User user = schoolClass.getUser();
			teacherResponse = UserResponse.builder()
					.id(user.getId())
					.fullName(user.getFullName())
					.email(user.getEmail())
					.phone(user.getPhone())
					.role(user.getRole())
					.build();
		}
		return SchoolClassResponse.builder()
				.id(schoolClass.getId())
				.className(schoolClass.getClassName())
				.description(schoolClass.getDescription())
				.active(schoolClass.getActive())
				.classTeacher(teacherResponse)
				.build();
	}

}