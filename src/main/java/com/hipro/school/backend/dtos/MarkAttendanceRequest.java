package com.hipro.school.backend.dtos;

import com.hipro.school.backend.enums.AttendanceSource;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MarkAttendanceRequest {

	private String qrCodeValue;

	private AttendanceSource attendanceSource;

	private Double latitude;

	private Double longitude;
}