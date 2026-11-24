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
@Table(name = "website_floating_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebsiteFloatingSetting {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private Long id;

	@Column(nullable = false)
	@Builder.Default
	private Boolean backToTopEnabled = true;

	@Column(nullable = false)
	@Builder.Default
	private Boolean whatsappEnabled = true;

	@Column(length = 20)
	private String whatsappPhone;

	@Column(length = 500)
	private String whatsappMessage;

	@Column(nullable = false)
	@Builder.Default
	private Boolean quickEnquiryEnabled = true;

	@Column(length = 100)
	private String quickEnquiryLabel;

	@Column(length = 500)
	private String quickEnquiryUrl;
}
