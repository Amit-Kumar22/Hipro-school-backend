package com.hipro.school.backend.services.ai.search;

import java.time.LocalDate;
import java.time.ZoneId;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hipro.school.backend.config.AiProperties;
import com.hipro.school.backend.exceptions.ServiceException;
import com.hipro.school.backend.services.ai.AiClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SqlGenerationService {

	private static final String SQL_GENERATION_PROMPT = """
			You are a MySQL database query generator. Given a database schema and a user question,
			generate a single MySQL-compatible SELECT query that answers the question.

			Rules:
			- Respond with ONLY a valid JSON object. No markdown, no code fences, no explanation.
			- JSON shape: {"sql":"SELECT ...","explanation":"brief description"}
			- Only use tables and columns that exist in the provided schema.
			- Use proper JOINs based on the foreign key relationships shown.
			- For date filters, use the current date provided in the user message.
			- "this month" means the current calendar month; "last month" means the previous month.
			- Use LIMIT for large result sets (default 100 rows).
			- Use aggregate functions (SUM, COUNT, AVG, MAX, MIN) when the question asks for totals or statistics.
			- Use GROUP BY when the question asks for per-group breakdowns.
			- Use ORDER BY with DESC for "top" or "highest" queries.
			- Always alias aggregate columns (e.g., SUM(amount) AS total_amount).
			- If the question cannot be answered with the available schema, return:
			  {"sql":null,"explanation":"The available schema does not contain the data needed to answer this question."}
			""";

	private final AiClient aiClient;
	private final ObjectMapper objectMapper;
	private final AiProperties aiProperties;

	public GeneratedSql generate(String question, String schemaDescription, String provider, LocalDate today,
			ZoneId timezone) {
		String userPrompt = buildPrompt(question, schemaDescription, today, timezone);

		String raw = aiClient.chat(SQL_GENERATION_PROMPT, userPrompt, provider);
		try {
			return parseResponse(raw);
		} catch (Exception firstAttempt) {
			log.warn("SQL generation parse failed, retrying with feedback: {}", firstAttempt.getMessage());
			String retryPrompt = userPrompt + "\n\nYour previous response was invalid: " + firstAttempt.getMessage()
					+ "\nRespond again with ONLY the JSON object.";
			String retryRaw = aiClient.chat(SQL_GENERATION_PROMPT, retryPrompt, provider);
			try {
				return parseResponse(retryRaw);
			} catch (Exception secondAttempt) {
				throw new ServiceException("Unable to generate a valid SQL query. Please rephrase your question.");
			}
		}
	}

	private String buildPrompt(String question, String schemaDescription, LocalDate today, ZoneId timezone) {
		StringBuilder prompt = new StringBuilder();
		prompt.append("Current date: ").append(today).append('\n');
		prompt.append("Timezone: ").append(timezone.getId()).append('\n');
		prompt.append('\n');
		prompt.append(schemaDescription);
		prompt.append('\n');
		prompt.append("User question: ").append(question);
		return prompt.toString();
	}

	private GeneratedSql parseResponse(String raw) throws Exception {
		String cleaned = raw.replace("```json", "").replace("```", "").trim();
		JsonNode node = objectMapper.readTree(cleaned);

		String sql = node.path("sql").asText(null);
		String explanation = node.path("explanation").asText("");

		if (sql != null) {
			sql = sql.trim();
			if (sql.endsWith(";")) {
				sql = sql.substring(0, sql.length() - 1).trim();
			}
		}

		return new GeneratedSql(sql, explanation);
	}

	public String summarize(String question, ReadOnlyQueryExecutor.ReadOnlyResultSet resultSet, String provider) {
		if (!aiProperties.isSummaryEnabled()) {
			return "Query returned " + resultSet.rowCount() + " row(s).";
		}
		try {
			String userPrompt = "Question: " + question + "\nColumns: " + resultSet.columns() + "\nRow count: "
					+ resultSet.rowCount() + "\nData (first 20 rows): "
					+ objectMapper.writeValueAsString(resultSet.rows().stream().limit(20).toList());
			String summaryPrompt = """
					You are a school management assistant. Summarize the query results for the user in at most
					3 sentences using the concrete numbers from the data. Plain text only, no markdown, no headings.
					If the result is empty, state that no matching records were found.
					""";
			return aiClient.chat(summaryPrompt, userPrompt, provider).trim();
		} catch (Exception e) {
			log.warn("AI summary failed, falling back to basic summary: {}", e.getMessage());
			return "Query returned " + resultSet.rowCount() + " row(s).";
		}
	}

	public record GeneratedSql(String sql, String explanation) {
	}
}
