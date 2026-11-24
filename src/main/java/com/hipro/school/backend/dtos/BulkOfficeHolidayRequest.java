package com.hipro.school.backend.dtos;

import java.time.LocalDate;

import jakarta.validation.constraints.AssertTrue;
import lombok.Data;

@Data
public class BulkOfficeHolidayRequest {

	private String holidayName;

	private LocalDate holidayDate;

	private String description;

	@AssertTrue(message = "Provide either master holiday ids OR holidayName and holidayDate")
	public boolean isValidHolidayRequest() {

		boolean hasCustomHoliday = holidayName != null && holidayDate != null;

		return hasCustomHoliday;
	}

}
