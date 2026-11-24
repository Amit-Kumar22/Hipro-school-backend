package com.hipro.school.backend.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.hipro.school.backend.enums.ExamStatus;
import com.hipro.school.backend.enums.ExamType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "exams", indexes = { @Index(name = "idx_exam_class", columnList = "class_id"),
		@Index(name = "idx_exam_type", columnList = "exam_type"),
		@Index(name = "idx_exam_status", columnList = "status"),
		@Index(name = "idx_exam_start_date", columnList = "start_date") })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Exam extends BaseEntity {

	@NotBlank
	@Size(max = 150)
	@Column(nullable = false, length = 150)
	private String title;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "exam_type", nullable = false, length = 40)
	private ExamType examType;
	
	@NotBlank
	@Column(nullable = false, length = 15)
	private String grade;

	@Size(max = 1000)
	@Column(length = 1000)
	private String description;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "class_id", nullable = false)
	private SchoolClass schoolClass;

	@NotNull
	@Column(name = "start_date", nullable = false)
	private LocalDateTime startDate;

	@NotNull
	@Column(name = "end_date", nullable = false)
	private LocalDateTime endDate;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private ExamStatus status;

	@Builder.Default
	@OneToMany(mappedBy = "exam", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("examDate ASC")
	private List<ExamSubject> examSubjects = new ArrayList<>();

	public void addExamSubject(ExamSubject examSubject) {
		examSubjects.add(examSubject);
		examSubject.setExam(this);
	}

	public void removeExamSubject(ExamSubject examSubject) {
		examSubjects.remove(examSubject);
		examSubject.setExam(null);
	}

	@Builder.Default
	@OneToMany(mappedBy = "exam", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ExamResult> results = new ArrayList<>();

	public void addResult(ExamResult result) {
		results.add(result);
		result.setExam(this);
	}

	public void removeResult(ExamResult result) {
		results.remove(result);
		result.setExam(null);
	}
}