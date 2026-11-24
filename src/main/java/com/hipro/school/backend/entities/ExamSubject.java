package com.hipro.school.backend.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "exam_subjects", uniqueConstraints = {
		@UniqueConstraint(name = "uk_exam_subject", columnNames = { "exam_id", "subject_id" }) }, indexes = {
				@Index(name = "idx_exam_subject_exam", columnList = "exam_id"),
				@Index(name = "idx_exam_subject_subject", columnList = "subject_id") })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamSubject extends BaseEntity {

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "exam_id", nullable = false)
	private Exam exam;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "subject_id", nullable = false)
	private Subject subject;

	@NotNull
	@Column(name = "exam_date", nullable = false)
	private LocalDateTime examDate;

	@NotNull
	@Column(name = "duration_minutes", nullable = false)
	private Integer durationMinutes;

	@NotNull
	@Column(name = "total_marks", nullable = false, precision = 10, scale = 2)
	private BigDecimal totalMarks;

	@NotNull
	@Column(name = "passing_marks", nullable = false, precision = 10, scale = 2)
	private BigDecimal passingMarks;
}