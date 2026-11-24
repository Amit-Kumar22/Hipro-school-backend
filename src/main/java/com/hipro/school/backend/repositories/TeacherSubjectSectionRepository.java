package com.hipro.school.backend.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hipro.school.backend.entities.TeacherSubjectSection;

@Repository
public interface TeacherSubjectSectionRepository extends JpaRepository<TeacherSubjectSection, Long> {

	Page<TeacherSubjectSection> findByTeacher_Id(Long teacherId, Pageable pageable);

	boolean existsByTeacher_IdAndSchoolClass_IdAndSubject_Id(Long teacherId, Long sectionId, Long subjectId);

	boolean existsByTeacher_IdAndSchoolClass_IdAndSubject_IdAndIdNot(Long teacherId, Long sectionId, Long subjectId,
			Long id);

}
