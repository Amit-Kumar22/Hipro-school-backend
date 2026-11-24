package com.hipro.school.backend.dtos;

import jakarta.validation.constraints.NotNull;
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
public class ClassSubjectRequest {

	@NotNull(message = "class is required.")
	private Long classId;

	@NotNull(message = "subject is required.")
	private Long subjectId;
}