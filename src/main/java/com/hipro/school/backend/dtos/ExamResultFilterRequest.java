package com.hipro.school.backend.dtos;

import com.hipro.school.backend.enums.ResultStatus;

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
public class ExamResultFilterRequest {

	private Long examId;

	private Long examSubjectId;

	private Long studentId;

	private Long classId;

	private ResultStatus status;

	private Boolean absent;

	private Boolean passed;
}