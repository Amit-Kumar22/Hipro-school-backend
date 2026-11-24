package com.hipro.school.backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "schools", indexes = {
		@Index(name = "idx_school_code", columnList = "school_code") }, uniqueConstraints = {
				@UniqueConstraint(name = "uk_school_code", columnNames = "school_code") })
@Getter
@Setter
public class School extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	@JsonIgnore
	private User user;

	@Column(name = "school_name", nullable = false)
	private String schoolName;

	@Column(name = "school_code", unique = true, nullable = false)
	private String schoolCode;

	@Column(name = "registration_number")
	private String registrationNumber;

	@Column(name = "affiliation_board")
	private String affiliationBoard; // CBSE / ICSE / State Board

	@Column(name = "established_year")
	private Integer establishedYear;
	
	@Column(name = "email_domain")
	private String EmailDomain;

	@Column(name = "email")
	private String email;

	@Column(name = "phone_number")
	private String phoneNumber;

	@Column(name = "alternate_phone")
	private String alternatePhone;

	@Column(name = "website")
	private String website;

	@Column(name = "logo_url")
	private String logoUrl;

	@Column(name = "principal_name")
	private String principalName;

	@Column(name = "total_students")
	private Integer totalStudents;

	@Column(name = "total_teachers")
	private Integer totalTeachers;

	@Embedded
	private Address address;
	
	@Column(name = "latitude")
	private Double latitude;

	@Column(name = "longitude")
	private Double longitude;

	@Column(name = "allowed_radius_meters")
	private Double allowedRadiusMeters;

	@Column(name = "qr_code_value")
	private String qrCodeValue; // Dynamic or static QR string

}