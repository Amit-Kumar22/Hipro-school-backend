package com.hipro.school.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceVerifyResponse {

	private Boolean allowed;

	private String message;

	private String checkInOutTime;

	private String distance;
}
