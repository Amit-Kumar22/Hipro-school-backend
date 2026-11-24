package com.hipro.school.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hipro.school.backend.dtos.dashboard.ClassStatsProjection;
import com.hipro.school.backend.entities.SchoolClass;

@Repository
public interface SchoolClassRepository extends JpaRepository<SchoolClass, Long> {

	@Query("""
			    SELECT sc  FROM SchoolClass sc  WHERE sc.user.id = :id
			""")
	Optional<SchoolClass> findByClassTeacherId(@Param("id") Long id);
	
	Optional<SchoolClass> findByUserId(Long userId);

	Optional<SchoolClass> findByClassNameIgnoreCase(String className);

	@Query("SELECT sc.id as classId, sc.className as className, COUNT(s.id) as studentCount FROM SchoolClass sc LEFT JOIN Student s ON s.schoolClass = sc GROUP BY sc.id, sc.className ORDER BY sc.className")
	List<ClassStatsProjection> findClassStatsWithStudentCount();
}
