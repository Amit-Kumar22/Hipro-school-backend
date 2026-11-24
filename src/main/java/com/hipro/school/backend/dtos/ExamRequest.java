package com.hipro.school.backend.dtos;

import java.time.LocalDateTime;

import com.hipro.school.backend.enums.ExamStatus;
import com.hipro.school.backend.enums.ExamType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class ExamRequest {

	@NotBlank
	@Size(max = 150)
	private String title;

	@NotNull
	private ExamType examType;

	@Size(max = 1000)
	private String description;

	@NotNull
	private Long classId;

	@NotNull
	private LocalDateTime startDate;

	@NotNull
	private LocalDateTime endDate;

	@NotNull
	private ExamStatus status;

	//@NotEmpty
	//@Valid
	//private List<ExamSubjectRequest> subjects;
}