package com.hipro.school.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.hipro.school.backend.entities.ClassSubject;

import io.lettuce.core.dynamic.annotation.Param;

public interface ClassSubjectRepository
		extends JpaRepository<ClassSubject, Long>, JpaSpecificationExecutor<ClassSubject> {

	boolean existsBySchoolClass_IdAndSubject_Id(Long classId, Long subjectId);

	boolean existsBySchoolClass_IdAndSubject_IdAndIdNot(Long classId, Long subjectId, Long id);

	List<ClassSubject> findAllBySchoolClass_IdOrderBySubject_SubjectNameAsc(Long classId);

	List<ClassSubject> findAllBySubject_IdOrderBySchoolClass_ClassNameAsc(Long subjectId);

	@Modifying
	@Query("""
			DELETE FROM ClassSubject cs WHERE cs.schoolClass.id = :classId
			""")
	void deleteAllByClassId(@Param("classId") Long classId);
}