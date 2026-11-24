package com.hipro.school.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.dtos.TSSRequest;
import com.hipro.school.backend.entities.Role;
import com.hipro.school.backend.entities.SchoolClass;
import com.hipro.school.backend.entities.Subject;
import com.hipro.school.backend.entities.TeacherSubjectSection;
import com.hipro.school.backend.entities.User;
import com.hipro.school.backend.exceptions.ResourceNotFoundException;
import com.hipro.school.backend.exceptions.ServiceException;
import com.hipro.school.backend.repositories.SchoolClassRepository;
import com.hipro.school.backend.repositories.SubjectRepository;
import com.hipro.school.backend.repositories.TeacherSubjectSectionRepository;
import com.hipro.school.backend.repositories.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TeacherSubjectSectionService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private SchoolClassRepository classRepository;

	@Autowired
	private SubjectRepository subjectRepository;

	@Autowired
	private TeacherSubjectSectionRepository tscRepository;

	public PageResponse<TeacherSubjectSection> getAll(String email, Pageable pageable) {
		log.info("Fetching Teacher Subject Section list for user: {}", email);
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));
		Page<TeacherSubjectSection> pages;
		if (user.getRole() == Role.TEACHER) {
			log.info("Role TEACHER - fetching mappings for teacher id: {}", user.getId());
			pages = tscRepository.findByTeacher_Id(user.getId(), pageable);
		} else {
			log.info("Role {} - fetching all mappings", user.getRole());
			pages = tscRepository.findAll(pageable);
		}
		return PageResponse.from(pages);
	}

	public PageResponse<TeacherSubjectSection> getAllDetail(Pageable pageable) {
		log.info("Fetching Teacher Subject Section list");
		Page<TeacherSubjectSection> pages = tscRepository.findAll(pageable);
		return PageResponse.from(pages);
	}

	public TeacherSubjectSection getById(Long id) {
		log.info("Fetching teacher subject section id: {}", id);
		return tscRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Teacher subject section not found. :: " + id));
	}

	public void create(TSSRequest req) {
		log.info("Creating teacher subject section for teacher id: {}", req.getTeacherId());
		User teacher = userRepository.findByIdAndRole(req.getTeacherId(), Role.TEACHER)
				.orElseThrow(() -> new ResourceNotFoundException("Teacher detail not found. :: " + req.getTeacherId()));

		Subject subject = subjectRepository.findById(req.getSubjectId())
				.orElseThrow(() -> new ResourceNotFoundException("Subject detail not found. :: " + req.getSubjectId()));

		SchoolClass cl = classRepository.findById(req.getClassId())
				.orElseThrow(() -> new ResourceNotFoundException("Section detail not found. :: " + req.getClassId()));

		if (tscRepository.existsByTeacher_IdAndSchoolClass_IdAndSubject_Id(teacher.getId(), cl.getId(),
				subject.getId())) {
			throw new ServiceException("Mapping already exists for teacher id: " + req.getTeacherId()
					+ ", section id: " + req.getClassId() + " and subject id: " + req.getSubjectId());
		}

		TeacherSubjectSection scs = new TeacherSubjectSection();
		scs.setTeacher(teacher);
		scs.setSchoolClass(cl);
		scs.setSubject(subject);
		tscRepository.save(scs);
	}

	public void update(Long id, TSSRequest req) {
		log.info("Updating teacher subject section id: {}", id);
		TeacherSubjectSection scs = getById(id);
		User teacher = userRepository.findByIdAndRole(req.getTeacherId(), Role.TEACHER)
				.orElseThrow(() -> new ResourceNotFoundException("Teacher detail not found. :: " + req.getTeacherId()));

		Subject subject = subjectRepository.findById(req.getSubjectId())
				.orElseThrow(() -> new ResourceNotFoundException("Subject detail not found. :: " + req.getSubjectId()));

		SchoolClass cl = classRepository.findById(req.getClassId())
				.orElseThrow(() -> new ResourceNotFoundException("Section detail not found. :: " + req.getClassId()));

		if (tscRepository.existsByTeacher_IdAndSchoolClass_IdAndSubject_IdAndIdNot(teacher.getId(), cl.getId(),
				subject.getId(), id)) {
			throw new ServiceException("Mapping already exists for teacher id: " + req.getTeacherId()
					+ ", section id: " + req.getClassId() + " and subject id: " + req.getSubjectId());
		}

		scs.setTeacher(teacher);
		scs.setSchoolClass(cl);
		scs.setSubject(subject);
		tscRepository.save(scs);

	}

	public void delete(Long id) {
		log.info("Deleting teacher subject section id: {}", id);
		TeacherSubjectSection scs = getById(id);
		tscRepository.delete(scs);
	}
}