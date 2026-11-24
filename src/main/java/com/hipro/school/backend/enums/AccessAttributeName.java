package com.hipro.school.backend.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum AccessAttributeName {

	// People / Users
	STUDENT("Student"),
	TEACHER("Teacher"),
	PARENT("Parent"),
	STAFF("Staff"),
	USER("User Management"),
	SCHOOL_LEAD("School Lead"),

	// Academics
	SCHOOL_CLASS("Class"),
	SECTION("Section"),
	SUBJECT("Subject"),
	EXAM("Exam"),
	QUESTION("Question"),
	HOMEWORK("Homework"),
	ATTENDANCE("Attendance"),
	TIMETABLE("Timetable"),

	// Fees & Finance
	FEE_STRUCTURE("Fee Structure"),
	STUDENT_FEE("Student Fee"),
	PAYMENT("Payment"),
	SALARY("Salary"),

	// Communication & Administration
	NOTICE("Notice / Announcement"),
	HOLIDAY("Holiday"),
	LEAVE("Student Leave"),
	CHAT("Chat & Message"),
	REPORT("Report"),
	DASHBOARD("Dashboard"),
	SETTINGS("Settings"),
	WEBSITE("Website / CMS"),
	AUDIT_LOG("Audit Log");

	@Getter
	private final String label;

	public static AccessAttributeName fromLabel(String label) {
		for (AccessAttributeName attribute : values()) {
			if (attribute.label.equalsIgnoreCase(label) || attribute.name().equalsIgnoreCase(label)) {
				return attribute;
			}
		}
		return null;
	}
}
