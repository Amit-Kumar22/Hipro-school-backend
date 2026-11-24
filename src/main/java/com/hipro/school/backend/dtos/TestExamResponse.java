package com.hipro.school.backend.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.hipro.school.backend.enums.ExamStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestExamResponse {

	private Long id;

	private String titleName;

	private String description;

	private Long schoolClassId;
	
	private String schoolClassName;

	private Long subjectId;
	private String subjectName;

	private Integer durationMinutes;

	private BigDecimal totalMarks;

	private BigDecimal passingMarks;

	private LocalDateTime startDate;

	private ExamStatus status;
}