package com.hipro.school.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hipro.school.backend.entities.Homework;

@Repository
public interface HomeworkRepository extends JpaRepository<Homework, Long>, JpaSpecificationExecutor<Homework> {

	@Query("SELECT h FROM Homework h JOIN h.weeklyTimetable tt WHERE tt.teacher.id = :teacherId ORDER BY h.homeworkDate DESC")
	List<Homework> findByTeacherIdOrderByHomeworkDateDesc(@Param("teacherId") Long teacherId);

	@Query("SELECT h FROM Homework h JOIN h.weeklyTimetable tt JOIN tt.period p WHERE p.schoolClass.id = :classId ORDER BY h.homeworkDate DESC")
	List<Homework> findByClassIdOrderByHomeworkDateDesc(@Param("classId") Long classId);
}
