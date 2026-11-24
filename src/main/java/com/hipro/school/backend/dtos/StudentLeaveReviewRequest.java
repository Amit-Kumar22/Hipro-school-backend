package com.hipro.school.backend.dtos;

import com.hipro.school.backend.enums.LeaveStatus;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentLeaveReviewRequest {

	@NotNull(message = "status is required")
	private LeaveStatus status;

	@Size(max = 500, message = "remarks must not exceed 500 characters")
	private String remarks;

}
