package com.hipro.school.backend.entities;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "holiday")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Holiday extends BaseEntity {

	// For custom office holidays
	private String holidayName;

	private LocalDate holidayDate;

	private String description;
}