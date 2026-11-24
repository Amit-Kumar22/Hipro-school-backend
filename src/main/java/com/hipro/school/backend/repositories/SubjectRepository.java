package com.hipro.school.backend.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.hipro.school.backend.entities.Subject;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {

	@Query("""
			SELECT s FROM Subject s
			WHERE (:subjectName IS NULL OR LOWER(s.subjectName) LIKE LOWER(CONCAT('%', :subjectName, '%')))
			""")
	Page<Subject> findAllBySubjectName(@Param("subjectName") String subjectName, Pageable pageable);

	Optional<Subject> findBySubjectCode(String subjectCode);

}
