package com.hipro.school.backend.dtos.dashboard;

public interface ClassStatsProjection {
	Long getClassId();
	String getClassName();
	long getStudentCount();
}
