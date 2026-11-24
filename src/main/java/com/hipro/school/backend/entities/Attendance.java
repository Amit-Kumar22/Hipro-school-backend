package com.hipro.school.backend.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hipro.school.backend.enums.AttendanceSource;
import com.hipro.school.backend.enums.AttendanceStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "attendance", indexes = { @Index(name = "idx_attendance_date", columnList = "attendanceDate"),
		@Index(name = "idx_attendance_user_date", columnList = "user_id, attendanceDate") })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Attendance extends BaseEntity {

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id")
	@JsonIgnore
	@ToString.Exclude
	private User user;

	@Column(name = "name", length = 50)
	private String name;

	@Column(name = "attendance_date", nullable = false)
	private LocalDate attendanceDate;

	@JsonFormat(pattern = "d MMMM yyyy hh:mm a", timezone = "Asia/Kolkata")
	private LocalDateTime loginTime;

	@JsonFormat(pattern = "d MMMM yyyy hh:mm a", timezone = "Asia/Kolkata")
	private LocalDateTime logoutTime;

	private Double checkinScanLatitude;

	private Double checkinScanLongitude;

	private Double checkoutScanLatitude;

	private Double checkoutScanLongitude;

	private String attendanceQRCode;

	private String checkIndistanceFromOffice;

	private String checkOutdistanceFromOffice;

//	@OneToMany(mappedBy = "attendance", cascade = CascadeType.ALL, orphanRemoval = true)
//	private List<BreakTime> breaks = new ArrayList<>();

	private Long totalWorkingMinutes; // e.g., 480 = 8 hours

	private Long minutesLate;

	@Enumerated(EnumType.STRING)
	private AttendanceStatus status; // PRESENT, ABSENT, HALF_DAY, LATE

	@Enumerated(EnumType.STRING)
	private AttendanceSource attendanceSource;

	@Column(length = 255)
	private String remarks;
	
	@Column(length = 50)
	private String updateBy;

}
