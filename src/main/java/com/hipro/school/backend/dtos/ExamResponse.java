package com.hipro.school.backend.dtos;

import java.time.LocalDateTime;
import java.util.List;

import com.hipro.school.backend.enums.ExamStatus;
import com.hipro.school.backend.enums.ExamType;

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
public class ExamResponse {

	private Long id;

	private String title;

	private ExamType examType;

	private String description;

	private Long classId;

	private String className;

	private LocalDateTime startDate;

	private LocalDateTime endDate;

	private ExamStatus status;

	private List<ExamSubjectResponse> subjects;

	private LocalDateTime createdAt;
}