package com.hipro.school.backend.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "student_answers", uniqueConstraints = {
		@UniqueConstraint(name = "uk_student_exam_question", columnNames = { "student_exam_id", "exam_question_id" }) })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentAnswer extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "student_exam_id", nullable = false)
	private StudentExam studentExam;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "exam_question_id", nullable = false)
	private ExamQuestion examQuestion;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "selected_option_id")
	private QuestionOption selectedOption;

	@Column(columnDefinition = "TEXT")
	private String answerText;

	@Column(nullable = false)
	private Boolean correct = false;

	@Column(precision = 10, scale = 2)
	private BigDecimal marksObtained;

	private LocalDateTime answeredAt;

}