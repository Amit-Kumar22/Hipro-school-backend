package com.hipro.school.backend.services;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.hipro.school.backend.dtos.ClassWiseStudentResponse;
import com.hipro.school.backend.dtos.CustomerResponse;
import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.dtos.StudentClassRequest;
import com.hipro.school.backend.entities.SchoolClass;
import com.hipro.school.backend.entities.StudentClassSection;
import com.hipro.school.backend.entities.User;
import com.hipro.school.backend.exceptions.ResourceNotFoundException;
import com.hipro.school.backend.repositories.SchoolClassRepository;
import com.hipro.school.backend.repositories.StudentClassSectionRepository;
import com.hipro.school.backend.repositories.UserRepository;

import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class StudentClassSectionService {

	@Autowired
	private StudentClassSectionRepository repository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private SchoolClassRepository schoolClassRepository;

	@Autowired
	private ModelMapper modelMapper;

	public PageResponse<StudentClassSection> getAll(Pageable pageable) {
		log.info("Fetching student class section list");
		Page<StudentClassSection> pages = repository.findAll(pageable);
		return PageResponse.from(pages);
	}

	public PageResponse<ClassWiseStudentResponse> getStudentsByClass(Long classId, Long sectionId, Pageable pageable) {
		log.info("Fetching students by class id: {}, section id: {} with pageable: {}", classId, sectionId, pageable);
		Page<StudentClassSection> page = repository.findAll(byFilters(classId, sectionId), pageable);
		Page<ClassWiseStudentResponse> mappedPage = page.map(s -> {
			ClassWiseStudentResponse res = new ClassWiseStudentResponse();
			res.setClassId(s.getSchoolClass().getId());
			res.setClassName(s.getSchoolClass().getClassName());
			res.setSectionName(s.getSchoolClass().getClassName());
			res.setAcademicYear(s.getAcademicYear());

			CustomerResponse student = modelMapper.map(s.getStudent(), CustomerResponse.class);
			res.setStudents(List.of(student));
			return res;
		});
		return PageResponse.from(mappedPage);
	}

	public StudentClassSection getById(Long id) {
		log.info("Fetching student class section id: {}", id);
		return repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Student class section not found"));
	}

	public StudentClassSection create(StudentClassRequest req) {
		log.info("Creating student class section for student id: {}", req.getStudentId());
		User student = userRepository.findById(req.getStudentId())
				.orElseThrow(() -> new ResourceNotFoundException("Student not found"));

		SchoolClass schoolClass = schoolClassRepository.findById(req.getClassId())
				.orElseThrow(() -> new ResourceNotFoundException("Class not found"));

		StudentClassSection scs = new StudentClassSection();
		scs.setStudent(student);
		scs.setSchoolClass(schoolClass);
		scs.setAcademicYear(req.getAcademicYear());
		return repository.save(scs);
	}

	public StudentClassSection update(Long id, StudentClassRequest req) {
		log.info("Updating student class section id: {}", id);
		StudentClassSection scs = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Student class section not found"));

		SchoolClass schoolClass = schoolClassRepository.findById(req.getClassId())
				.orElseThrow(() -> new ResourceNotFoundException("Class not found"));

		scs.setSchoolClass(schoolClass);
		scs.setAcademicYear(req.getAcademicYear());
		return repository.save(scs);
	}

	public void delete(Long id) {
		log.info("Deleting student class section id: {}", id);
		StudentClassSection scs = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Student class section not found"));
		repository.delete(scs);
	}

	public static Specification<StudentClassSection> byFilters(Long classId, Long sectionId) {

		return (root, query, cb) -> {

			List<Predicate> predicates = new ArrayList<>();

			// Required class filter.
			predicates.add(cb.equal(root.get("schoolClass").get("id"), classId));

			// Optional section filter.
			if (sectionId != null) {
				predicates.add(cb.equal(root.get("section").get("id"), sectionId));
			}

			query.orderBy(cb.asc(root.get("section").get("id")), cb.asc(root.get("student").get("id")));

			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}
}