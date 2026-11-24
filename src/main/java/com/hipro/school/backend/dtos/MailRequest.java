package com.hipro.school.backend.dtos;

import lombok.Data;

@Data
public class MailRequest {

	private String name;
	private String to;
	private String subject;
	private String token;

}
