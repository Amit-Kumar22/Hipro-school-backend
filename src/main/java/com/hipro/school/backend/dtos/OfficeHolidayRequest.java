package com.hipro.school.backend.dtos;

import java.time.LocalDate;

import lombok.Data;

@Data
public class OfficeHolidayRequest {

	private String holidayName;

	private LocalDate holidayDate;

	private String description;

}
