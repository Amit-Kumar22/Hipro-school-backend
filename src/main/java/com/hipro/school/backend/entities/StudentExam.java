package com.hipro.school.backend.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.hipro.school.backend.enums.StudentExamStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "student_exams", uniqueConstraints = {
		@UniqueConstraint(name = "uk_student_exam", columnNames = { "student_id", "exam_id" }) })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentExam extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "student_id", nullable = false)
	private User student;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "exam_id", nullable = false)
	private TestExam exam;

	private LocalDateTime startedAt;

	private LocalDateTime submittedAt;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private StudentExamStatus status;

	@Column(precision = 10, scale = 2)
	private BigDecimal totalMarks;

	@Column(precision = 10, scale = 2)
	private BigDecimal obtainedMarks;

	@Column(precision = 10, scale = 2)
	private BigDecimal percentage;

	@Column(nullable = false)
	private Integer attemptNumber = 1;

}