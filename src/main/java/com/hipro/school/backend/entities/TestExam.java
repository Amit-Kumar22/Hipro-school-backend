package com.hipro.school.backend.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.hipro.school.backend.enums.ExamStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "test_exam")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestExam extends BaseEntity {

	@Column(nullable = false, length = 150)
	private String titleName;

	@Column(length = 500)
	private String description;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "class_id", nullable = false)
	private SchoolClass schoolClass;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "subject_id", nullable = false)
	private Subject subject;

	@Column(nullable = false)
	private Integer durationMinutes;

	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal totalMarks;

	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal passingMarks;

	private LocalDateTime startDate;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private ExamStatus status;

}