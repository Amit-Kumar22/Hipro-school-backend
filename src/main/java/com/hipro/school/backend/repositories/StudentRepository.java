package com.hipro.school.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hipro.school.backend.entities.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long>, JpaSpecificationExecutor<Student> {

	@Query(value = """
			SELECT * FROM students ORDER BY CAST(admission_number AS UNSIGNED)
			DESC LIMIT 1
			""", nativeQuery = true)
	Optional<Student> findLastAdmissionNumber();

	Optional<Student> findTopByOrderByCreatedDescIdDesc();

	List<Student> findAllBySchoolClass_Id(Long classId);

	Page<Student> findAllBySchoolClass_Id(Long classId, Pageable pageable);

	long countBySchoolClass_Id(Long classId);

	Optional<Student> findByStudentUser_Id(Long userId);

	List<Student> findByParentUser_Id(Long userId);

	@Query("SELECT s FROM Student s WHERE s.studentUser.email = :email")
	Optional<Student> findByStudentUserEmail(@Param("email") String email);

	@Query("SELECT s FROM Student s WHERE s.parentUser.email = :email")
	List<Student> findByParentUserEmail(@Param("email") String email);
}
