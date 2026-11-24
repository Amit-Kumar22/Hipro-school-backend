package com.hipro.school.backend.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.hipro.school.backend.entities.StudentClassSection;

public interface StudentClassSectionRepository
		extends JpaRepository<StudentClassSection, Long>, JpaSpecificationExecutor<StudentClassSection> {

	Optional<StudentClassSection> findByStudentIdAndAcademicYear(Long studentId, String academicYear);

	Page<StudentClassSection> findBySchoolClassId(Long classId, Pageable pageable);

	@Query("""
			SELECT scs FROM StudentClassSection scs  WHERE scs.schoolClass.id = :classId
			AND (:academicYear IS NULL OR scs.academicYear = :academicYear)
			""")
	Page<StudentClassSection> findBySchoolClassIdAndAcademicYear(Long classId, String academicYear,
			Pageable pageable);

	@Override
	@EntityGraph(attributePaths = { "student", "schoolClass", "section" })
	Page<StudentClassSection> findAll(Specification<StudentClassSection> spec, Pageable pageable);

}
