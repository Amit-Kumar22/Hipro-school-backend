package com.hipro.school.backend.entities;

import java.time.LocalDateTime;

import com.hipro.school.backend.enums.LeadStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "leads", indexes = { @Index(name = "idx_school_lead_name", columnList = "school_name"),
		@Index(name = "idx_school_lead_city", columnList = "city"),
		@Index(name = "idx_school_lead_phone", columnList = "phone"),
		@Index(name = "idx_school_lead_status", columnList = "status") })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchoolLead extends BaseEntity {

	@Column(name = "school_name", nullable = false, length = 255)
	private String schoolName;

	@Column(length = 500)
	private String address;

	@Column(length = 500)
	private String locality;

	@Column(length = 100)
	private String city;

	@Column(length = 100)
	private String state;

	@Column(length = 20)
	private String pincode;

	@Column(length = 30)
	private String phone;

	@Column(length = 30)
	private String alternatePhone;

	@Column(length = 255)
	private String email;

	@Column(length = 500)
	private String website;

	@Column(length = 255)
	private String category;

	@Column(length = 255)
	private String board;

	@Column(length = 100)
	private String schoolType;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	@Builder.Default
	private LeadStatus status = LeadStatus.NEW;

	@Column(length = 1000)
	private String notes;

	private LocalDateTime lastContactedAt;

	private LocalDateTime nextFollowUpAt;
}