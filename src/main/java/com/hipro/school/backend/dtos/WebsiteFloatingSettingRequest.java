package com.hipro.school.backend.dtos;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebsiteFloatingSettingRequest {

	private Boolean backToTopEnabled;

	private Boolean whatsappEnabled;

	@Size(max = 20, message = "Whatsapp phone must not exceed 20 characters")
	private String whatsappPhone;

	@Size(max = 500, message = "Whatsapp message must not exceed 500 characters")
	private String whatsappMessage;

	private Boolean quickEnquiryEnabled;

	@Size(max = 100, message = "Quick enquiry label must not exceed 100 characters")
	private String quickEnquiryLabel;

	@Size(max = 500, message = "Quick enquiry url must not exceed 500 characters")
	private String quickEnquiryUrl;
}
