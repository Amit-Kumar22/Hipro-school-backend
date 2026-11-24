package com.hipro.school.backend.dtos;

import java.time.LocalDate;

import com.hipro.school.backend.enums.LeaveType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentLeaveRequest {

	@NotBlank(message = "schoolCode is required")
	private String schoolCode;

	@NotNull(message = "studentId is required")
	private Long studentId;

	@NotNull(message = "leaveType is required")
	private LeaveType leaveType;

	@NotNull(message = "fromDate is required")
	private LocalDate fromDate;

	@NotNull(message = "toDate is required")
	private LocalDate toDate;

	@NotBlank(message = "reason is required")
	@Size(max = 500, message = "reason must not exceed 500 characters")
	private String reason;

}
