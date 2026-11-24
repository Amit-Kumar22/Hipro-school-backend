package com.hipro.school.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.hipro.school.backend.entities.Teacher;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long>, JpaSpecificationExecutor<Teacher> {

	Optional<Teacher> findByTeacherUserEmail(String email);

	Optional<Teacher> findByTeacherUser_Id(Long userId);

	List<Teacher> findAllBySubjectId(Long subjectId);
}