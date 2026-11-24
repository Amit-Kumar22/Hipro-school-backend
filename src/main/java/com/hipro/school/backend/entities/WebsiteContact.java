package com.hipro.school.backend.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "website_contacts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebsiteContact {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private Long id;

	@Column(length = 20)
	private String phone;

	@Column(length = 150)
	private String email;

	@Column(length = 150)
	private String addressLine1;

	@Column(length = 150)
	private String addressLine2;

	@Column(length = 100)
	private String city;

	@Column(length = 100)
	private String state;

	@Column(length = 20)
	private String pincode;

	@Column(length = 100)
	@Builder.Default
	private String country = "India";

	@Column(length = 100)
	private String workingDays;

	@Column(length = 100)
	private String workingTime;
}
