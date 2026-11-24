package com.hipro.school.backend.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Address {

	@Column(length = 100)
	private String buildingName;

	@Column(length = 100)
	private String streetName;

	@Column(length = 100)
	private String landmark;

	@Column(length = 50)
	private String district;

	@Column(length = 50)
	private String city;

	@Column(length = 10)
	private String pin;

	@Column(length = 50)
	private String stateName;


}
