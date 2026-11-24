package com.hipro.school.backend.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "contact_us")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContactUs extends BaseEntity {

	//private String type;

	@Column(name = "local_phone_numbers")
	private String localPhoneNumbers;

	@Column(name = "local_emails")
	private String localEmails;

	@Column(name = "foreign_phone_numbers")
	private String foreignPhoneNumbers;

	@Column(name = "foreign_emails")
	private String foreignEmails;


}
