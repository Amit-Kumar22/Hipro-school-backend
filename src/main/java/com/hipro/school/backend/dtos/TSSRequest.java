package com.hipro.school.backend.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TSSRequest {
	private Long subjectId;
	private Long classId;
	private Integer teacherId;
}