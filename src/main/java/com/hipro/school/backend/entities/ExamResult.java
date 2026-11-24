package com.hipro.school.backend.entities;

import java.math.BigDecimal;

import com.hipro.school.backend.enums.ResultStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "exam_results", uniqueConstraints = {
		@UniqueConstraint(name = "uk_exam_result_exam_subject_student", columnNames = { "exam_id", "exam_subject_id",
				"student_id" }) }, indexes = { @Index(name = "idx_exam_result_exam", columnList = "exam_id"),
						@Index(name = "idx_exam_result_exam_subject", columnList = "exam_subject_id"),
						@Index(name = "idx_exam_result_student", columnList = "student_id"),
						@Index(name = "idx_exam_result_exam_student", columnList = "exam_id, student_id") })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamResult extends BaseEntity {

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "exam_id", nullable = false)
	private Exam exam;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "exam_subject_id", nullable = false)
	private ExamSubject examSubject;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "student_id", nullable = false)
	private Student student;

	@NotNull
	@Column(name = "total_marks", nullable = false, precision = 10, scale = 2)
	private BigDecimal totalMarks;

	@NotNull
	@Column(name = "passing_marks", nullable = false, precision = 10, scale = 2)
	private BigDecimal passingMarks;

	@Column(name = "obtained_marks", precision = 10, scale = 2)
	private BigDecimal obtainedMarks;

	@Column(name = "percentage", precision = 5, scale = 2)
	private BigDecimal percentage;

	@Column(name = "grade", length = 10)
	private String grade;

	@NotNull
	@Builder.Default
	@Column(name = "is_absent", nullable = false)
	private Boolean absent = false;

	@NotNull
	@Builder.Default
	@Column(name = "is_passed", nullable = false)
	private Boolean passed = false;

	@Column(name = "remarks", length = 500)
	private String remarks;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 20)
	@Builder.Default
	private ResultStatus status = ResultStatus.DRAFT;
}