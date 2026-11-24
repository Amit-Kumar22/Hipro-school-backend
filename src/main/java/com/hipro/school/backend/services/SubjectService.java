package com.hipro.school.backend.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.dtos.SubjectRequest;
import com.hipro.school.backend.entities.Subject;
import com.hipro.school.backend.exceptions.ResourceNotFoundException;
import com.hipro.school.backend.repositories.SubjectRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SubjectService {

	@Autowired
	private SubjectRepository subjectRepository;

	public PageResponse<Subject> getAllSubject(Pageable pageable, String subjectName) {
		log.info("Fetching all subject");
		return PageResponse.from(subjectRepository.findAllBySubjectName(subjectName, pageable));
	}

	public Subject getSubjectById(Long subjectId) {
		log.info("Fetching subject for id: {}", subjectId);
		return subjectRepository.findById(subjectId)
				.orElseThrow(() -> new ResourceNotFoundException("Subject detail not found with id : " + subjectId));
	}

	public void saveSubject(SubjectRequest request) {
		log.info("Creating Subject: {}", request);
		Optional<Subject> subject = subjectRepository.findBySubjectCode(request.getSubjectCode());
		if (subject.isPresent()) {
			throw new ResourceNotFoundException(
					"Subject detail already exists with code : " + request.getSubjectCode());

		} else {
			log.info("Creating subject: {}", request);
			Subject sc = new Subject();
			sc.setSubjectCode(request.getSubjectCode());
			sc.setSubjectName(request.getSubjectName().toUpperCase());
			subjectRepository.save(sc);
		}

	}

	public void updateSubject(Long id, SubjectRequest request) {
		log.info("Updating Subject id: {}", id);
		Subject sc = getSubjectById(id);
		sc.setSubjectCode(request.getSubjectCode());
		sc.setSubjectName(request.getSubjectName().toUpperCase());
		subjectRepository.save(sc);
	}

	public void deleteSubject(Long id) {
		log.info("Deleting Subject id: {}", id);
		Subject sc = getSubjectById(id);
		subjectRepository.save(sc);
	}

}