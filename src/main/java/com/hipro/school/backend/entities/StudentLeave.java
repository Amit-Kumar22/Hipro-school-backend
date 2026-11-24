package com.hipro.school.backend.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hipro.school.backend.enums.LeaveStatus;
import com.hipro.school.backend.enums.LeaveType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "student_leaves", indexes = {
		@Index(name = "idx_student_leave_student", columnList = "student_id"),
		@Index(name = "idx_student_leave_status", columnList = "status"),
		@Index(name = "idx_student_leave_dates", columnList = "from_date, to_date") })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentLeave extends BaseEntity {

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "school_id")
	@JsonIgnore
	private School school;

	@NotNull
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "student_id", nullable = false, foreignKey = @ForeignKey(name = "fk_student_leave_student"))
	private Student student;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "leave_type", nullable = false, length = 30)
	private LeaveType leaveType;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	@Builder.Default
	private LeaveStatus status = LeaveStatus.PENDING;

	@NotNull
	@Column(name = "from_date", nullable = false)
	private LocalDate fromDate;

	@NotNull
	@Column(name = "to_date", nullable = false)
	private LocalDate toDate;

	@Column(nullable = false)
	@Builder.Default
	private Integer numberOfDays = 1;

	@NotNull
	@Size(min = 5, max = 500)
	@Column(nullable = false, length = 500)
	private String reason;

	@Size(max = 500)
	@Column(length = 500)
	private String remarks;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "reviewed_by")
	private User reviewedBy;

	@Column(name = "reviewed_at")
	private LocalDateTime reviewedAt;

}
