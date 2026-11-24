package com.hipro.school.backend.services;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.hipro.school.backend.dtos.MailRequest;
import com.hipro.school.backend.dtos.MailResponse;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EmailService {

	@Autowired
	private JavaMailSender sender;

	@Autowired
	private Configuration config;

	@Value("${application.mail.username}")
	private String ADMIN_EMAIL;

	public MailResponse sendFranchiseWelcomeEmail(MailRequest request) {

		log.info("Sending Franchise Welcome email to: {}", request.getTo());

		MailResponse response = new MailResponse();
		MimeMessage message = sender.createMimeMessage();
		try {

			MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
					StandardCharsets.UTF_8.name());
			// Template Model
			Map<String, Object> model = new HashMap<>();
			model.put("ownerName", request.getName());
			model.put("email", request.getTo());
			model.put("franchiseName", request.getToken());
			model.put("loginUrl", "https://yourdomain.com/login");

			Template template = config.getTemplate("franchiseWelcomeTemplate.ftl");
			String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);

			helper.setTo(request.getTo());
			helper.setSubject("Welcome to Khana Mart Franchise Program");
			helper.setText(html, true);
			helper.setFrom(ADMIN_EMAIL);

			sender.send(message);

			log.info("Franchise Welcome email sent successfully to {}", request.getTo());

			response.setStatus(Boolean.TRUE);
			response.setMessage("Welcome email sent successfully");

		} catch (Exception e) {

			log.error("Failed to send Franchise Welcome email to {}", request.getTo(), e);

			response.setStatus(Boolean.FALSE);
			response.setMessage("Email sending failed: " + e.getMessage());
		}

		return response;
	}

	public MailResponse sendEmail1(MailRequest request, Map<String, Object> model) {
		MailResponse response = new MailResponse();
		MimeMessage message = sender.createMimeMessage();

		try {
			// set mediaType
			MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
					StandardCharsets.UTF_8.name());
			model.put("Name", request.getName());
			// model.put("location", "Bangalore,India");
			model.put("code", request.getToken());
			Template t = config.getTemplate("registerEmail.ftl");
			String html = FreeMarkerTemplateUtils.processTemplateIntoString(t, model);

			helper.setTo(request.getTo());
			helper.setText(html, true);
			helper.setSubject(request.getSubject());
			helper.setFrom(ADMIN_EMAIL);
			sender.send(message);

			response.setMessage("mail send to : " + request.getTo());
			response.setStatus(Boolean.TRUE);

		} catch (MessagingException | IOException | TemplateException e) {
			response.setMessage("Mail Sending failure : " + e.getMessage());
			response.setStatus(Boolean.FALSE);
		}

		return response;
	}

	public MailResponse sendDailyLeadAlertEmail(MailRequest request, Map<String, Object> model, String[] ccEmails) {
		MailResponse response = new MailResponse();
		MimeMessage message = sender.createMimeMessage();

		try {
			// Prepare MimeMessageHelper for HTML content
			MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
					StandardCharsets.UTF_8.name());

			// Add dynamic values to the model, override if present
			model.put("staffName", request.getName()); // e.g. "John Doe"
			model.put("leadCount", model.getOrDefault("leadCount", 0)); // must be passed in model

			// Load your FreeMarker template for daily lead alert
			Template template = config.getTemplate("lowLeadEmail.ftl");
			log.info("Loaded template: lowLeadEmail.ftl");

			// Process template with the model
			String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
			log.info("Processed HTML content for daily lead alert email");
			// Set email details
			helper.setTo(request.getTo());

			if (ccEmails != null && ccEmails.length > 0) {
				log.info("Setting CC emails: {}", String.join(", ", ccEmails));
				helper.setCc(ccEmails);
			}

			helper.setText(html, true); // true = HTML content
			helper.setSubject(request.getSubject());
			helper.setFrom(ADMIN_EMAIL);

			// Send email
			sender.send(message);

			response.setMessage("Mail sent to: " + request.getTo());
			response.setStatus(Boolean.TRUE);
			log.info("Daily lead alert email sent to: {}", response);

		} catch (MessagingException | IOException | TemplateException e) {
			response.setMessage("Mail Sending failure: " + e.getMessage());
			response.setStatus(Boolean.FALSE);
			log.error("Failed to send daily lead alert email to {}: {}", request.getTo(), e.getMessage());
		}

		return response;
	}

	public MailResponse sendEmailForget1(MailRequest request, Map<String, Object> model) {
		MailResponse response = new MailResponse();
		MimeMessage message = sender.createMimeMessage();

		try {
			// set mediaType
			MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
					StandardCharsets.UTF_8.name());
			model.put("Name", request.getName());
			// model.put("location", "Bangalore,India");
			model.put("code", request.getToken());
			// add attachment
			// if() {
			// helper.addAttachment("logo.png", new ClassPathResource("logo.png"));
			// }
			Template t = config.getTemplate("resetPasswordCodeEmail.ftl");
			String html = FreeMarkerTemplateUtils.processTemplateIntoString(t, model);

			helper.setTo(request.getTo());
			helper.setText(html, true);
			helper.setSubject(request.getSubject());
			helper.setFrom(ADMIN_EMAIL);
			sender.send(message);

			response.setMessage("mail send to : " + request.getTo());
			response.setStatus(Boolean.TRUE);

		} catch (MessagingException | IOException | TemplateException e) {
			response.setMessage("Mail Sending failure : " + e.getMessage());
			response.setStatus(Boolean.FALSE);
		}

		return response;
	}

	public MailResponse sendEmailToParent(MailRequest request, Map<String, Object> model) {
		MailResponse response = new MailResponse();
		MimeMessage message = sender.createMimeMessage();
		try {
			MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
					StandardCharsets.UTF_8.name());
			Template template = config.getTemplate("parentEmail.ftl");
			String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
			helper.setTo(request.getTo());
			helper.setSubject(request.getSubject());
			helper.setText(html, true);
			helper.setFrom(ADMIN_EMAIL);
			sender.send(message);
			response.setMessage("Mail sent successfully to : " + request.getTo());
			response.setStatus(Boolean.TRUE);
		} catch (MessagingException | IOException | TemplateException e) {
			response.setMessage("Mail sending failure : " + e.getMessage());
			response.setStatus(Boolean.FALSE);
		}
		return response;
	}

	public MailResponse sendEmail(MailRequest request, Map<String, Object> model) {
		MailResponse response = new MailResponse();
		MimeMessage message = sender.createMimeMessage();

		try {
			// set mediaType
			MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
					StandardCharsets.UTF_8.name());
			model.put("Name", request.getName());
			// model.put("location", "Bangalore,India");
			model.put("code", request.getToken());
			Template t = config.getTemplate("registerEmail.ftl");
			String html = FreeMarkerTemplateUtils.processTemplateIntoString(t, model);

			helper.setTo(request.getTo());
			helper.setText(html, true);
			helper.setSubject(request.getSubject());
			helper.setFrom(ADMIN_EMAIL);
			sender.send(message);

			response.setMessage("mail send to : " + request.getTo());
			response.setStatus(Boolean.TRUE);

		} catch (MessagingException | IOException | TemplateException e) {
			response.setMessage("Mail Sending failure : " + e.getMessage());
			response.setStatus(Boolean.FALSE);
		}

		return response;
	}

	public MailResponse sendEmailForget(MailRequest request, Map<String, Object> model) {
		log.info("Sending forget-password email to: {}", request.getTo());
		MailResponse response = new MailResponse();
		MimeMessage message = sender.createMimeMessage();

		try {
			// set mediaType
			MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
					StandardCharsets.UTF_8.name());
			model.put("Name", request.getName());
			// model.put("location", "Bangalore,India");
			model.put("code", request.getToken());
			// add attachment
			// if() {
			// helper.addAttachment("logo.png", new ClassPathResource("logo.png"));
			// }
			Template t = config.getTemplate("resetPasswordCodeEmail.ftl");
			String html = FreeMarkerTemplateUtils.processTemplateIntoString(t, model);

			helper.setTo(request.getTo());
			helper.setText(html, true);
			helper.setSubject(request.getSubject());
			helper.setFrom(ADMIN_EMAIL);
			sender.send(message);

			response.setMessage("mail send to : " + request.getTo());
			response.setStatus(Boolean.TRUE);

		} catch (MessagingException | IOException | TemplateException e) {
			response.setMessage("Mail Sending failure : " + e.getMessage());
			response.setStatus(Boolean.FALSE);
		}

		return response;
	}

	//

}
