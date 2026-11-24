package com.hipro.school.backend.services.ai;

import java.time.LocalDate;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hipro.school.backend.config.AiProperties;
import com.hipro.school.backend.dtos.ai.AiQueryRequest;
import com.hipro.school.backend.dtos.ai.AiQueryResponse;
import com.hipro.school.backend.dtos.ai.AiReportResult;
import com.hipro.school.backend.entities.Role;
import com.hipro.school.backend.entities.User;
import com.hipro.school.backend.enums.AiIntent;
import com.hipro.school.backend.exceptions.ResourceNotFoundException;
import com.hipro.school.backend.exceptions.ServiceException;
import com.hipro.school.backend.repositories.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiQueryService {

	private static final String INTENT_SYSTEM_PROMPT = """
			You are a school management data router. Convert the user question into exactly one JSON object.
			JSON shape: {"intent":"<INTENT>","params":{...},"confidence":<number between 0 and 1>}

			Available intents and params:
			1. FEE_REPORT -> {"yearMonth":"YYYY-MM","classId":number|null,"className":"string|null"}
			   Use for paid/due fee reports for a month (e.g. "this month", "October 2026", "fees of class 5 this month").
			2. FEE_SUMMARY -> {}
			   Use for overall fee collection totals across the school.
			3. FEE_DEFAULTERS -> {"classId":number|null,"className":"string|null","limit":number}
			   Use for students with unpaid, partial or overdue fees.
			4. ATTENDANCE_REPORT -> {"date":"YYYY-MM-DD"}
			   Use for attendance counts by status on a day.
			5. STUDENT_COUNT -> {"classId":number|null,"className":"string|null"}
			   Use for counting students.

			Rules:
			- Respond with ONLY a valid JSON object. No markdown, no code fences, no explanation.
			- If the question mentions no date, use the current date provided in the user message.
			- "this month" means the current month; "last month" means the previous month.
			- Set className only when a class name/label is mentioned in the question; otherwise null.
			- If the question matches no intent, respond exactly: {"intent":"UNKNOWN","params":{},"confidence":0}
			""";

	private static final String SUMMARY_SYSTEM_PROMPT = """
			You are a school management assistant. Summarize the given report data for the admin in at most
			3 sentences using the concrete numbers from the data. Plain text only, no markdown, no headings.
			""";

	private final AiClient aiClient;
	private final AiToolRegistry toolRegistry;
	private final UserRepository userRepository;
	private final ObjectMapper objectMapper;
	private final AiProperties properties;

	@Transactional(readOnly = true)
	public AiQueryResponse query(AiQueryRequest request, String email) {
		long startedAt = System.currentTimeMillis();
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
		if (!Role.ADMIN.equals(user.getRole()) && !Role.SUPERADMIN.equals(user.getRole())) {
			throw new AccessDeniedException("Only admin users can run AI queries");
		}

		LocalDate today = LocalDate.now();
		Extraction extraction = extractIntent(request, today);
		AiIntentHandler handler = toolRegistry.handler(extraction.intent());
		AiReportResult report = handler.handle(extraction.params(), new AiQueryContext(request.getClassId(), today));
		String answer = summarize(request.getQuestion(), report, request.getProvider());

		log.info("AI query resolved: intent={}, confidence={}, provider={}, processingMs={}", extraction.intent(),
				extraction.confidence(), request.getProvider(), System.currentTimeMillis() - startedAt);
		return AiQueryResponse.builder().question(request.getQuestion()).intent(extraction.intent().name())
				.confidence(extraction.confidence()).answer(answer).report(report)
				.processingMs(System.currentTimeMillis() - startedAt).build();
	}

	private Extraction extractIntent(AiQueryRequest request, LocalDate today) {
		String userPrompt = buildExtractPrompt(request, today);
		String raw = aiClient.chat(INTENT_SYSTEM_PROMPT, userPrompt, request.getProvider());
		try {
			return parseExtraction(raw);
		} catch (Exception firstAttempt) {
			log.warn("Intent extraction parse failed, retrying with feedback: {}", firstAttempt.getMessage());
			String retryPrompt = userPrompt + "\n\nYour previous response was invalid: " + firstAttempt.getMessage()
					+ "\nRespond again with ONLY the JSON object.";
			String retryRaw = aiClient.chat(INTENT_SYSTEM_PROMPT, retryPrompt, request.getProvider());
			try {
				return parseExtraction(retryRaw);
			} catch (Exception secondAttempt) {
				throw new ServiceException(
						"Unable to understand the question. Please rephrase it using one of: fee report, "
								+ "fee summary, fee defaulters, attendance report, student count.");
			}
		}
	}

	private String buildExtractPrompt(AiQueryRequest request, LocalDate today) {
		StringBuilder prompt = new StringBuilder();
		prompt.append("Current date: ").append(today).append('\n');
		if (request.getClassId() != null) {
			prompt.append("classId provided by the requester: ").append(request.getClassId()).append('\n');
		}
		prompt.append("Question: ").append(request.getQuestion());
		return prompt.toString();
	}

	private Extraction parseExtraction(String raw) throws JsonProcessingException {
		String cleaned = raw.replace("```json", "").replace("```", "").trim();
		JsonNode node = objectMapper.readTree(cleaned);
		String intentText = node.path("intent").asText("");
		AiIntent intent;
		try {
			intent = AiIntent.valueOf(intentText.trim().toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new IllegalStateException("Unknown intent: " + intentText);
		}
		if (intent == AiIntent.UNKNOWN) {
			throw new IllegalStateException("Question does not match any supported intent");
		}
		JsonNode params = node.path("params");
		return new Extraction(intent, params.isObject() ? params : objectMapper.createObjectNode(),
				node.path("confidence").asDouble(0));
	}

	private String summarize(String question, AiReportResult report, String provider) {
		if (!properties.isSummaryEnabled()) {
			return report.getTitle();
		}
		try {
			String userPrompt = "Question: " + question + "\nReport data: " + objectMapper.writeValueAsString(report);
			return aiClient.chat(SUMMARY_SYSTEM_PROMPT, userPrompt, provider).trim();
		} catch (Exception e) {
			log.warn("AI summary failed, falling back to report title: {}", e.getMessage());
			return report.getTitle();
		}
	}

	private record Extraction(AiIntent intent, JsonNode params, double confidence) {
	}
}
