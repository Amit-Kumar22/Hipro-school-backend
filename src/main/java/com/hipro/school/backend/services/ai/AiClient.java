package com.hipro.school.backend.services.ai;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hipro.school.backend.config.AiProperties;
import com.hipro.school.backend.exceptions.ServiceException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiClient {

	private static final String OPENAI_CHAT_ENDPOINT = "/chat/completions";
	private static final String ANTHROPIC_MESSAGES_ENDPOINT = "/v1/messages";
	private static final String ANTHROPIC_VERSION = "2023-06-01";
	private static final int DEFAULT_MAX_TOKENS = 2048;

	private final AiProperties properties;

	private final ObjectMapper objectMapper = new ObjectMapper();

	private final Map<String, HttpClient> clients = new ConcurrentHashMap<>();

	public String chat(String systemPrompt, String userPrompt, String providerName) {
		AiProperties.NamedProvider named = properties.resolve(providerName);
		AiProperties.Provider provider = named.provider();
		if (!StringUtils.hasText(provider.getModel())) {
			throw new ServiceException("AI provider '" + named.name() + "' has no model configured.");
		}
		HttpClient client = clientFor(named.name(), provider);

		int attempts = Math.max(1, provider.getMaxAttempts());
		for (int attempt = 1; attempt <= attempts; attempt++) {
			try {
				String content = switch (provider.getProtocol()) {
					case OPENAI -> callOpenAi(client, provider, systemPrompt, userPrompt);
					case ANTHROPIC -> callAnthropic(client, named.name(), provider, systemPrompt, userPrompt);
				};
				if (!StringUtils.hasText(content)) {
					throw new IllegalStateException("Empty response from AI provider '" + named.name() + "'");
				}
				return content;
			} catch (ServiceException e) {
				throw e;
			} catch (AiHttpException e) {
				int status = e.getStatusCode();
				if (status == 401 || status == 403) {
					throw new ServiceException("AI provider '" + named.name() + "' rejected credentials (HTTP " + status
							+ "). Check the api-key for this provider.");
				}
				if (status == 404) {
					throw new ServiceException("AI provider '" + named.name() + "' endpoint or model not found (HTTP 404). "
							+ "Check base-url and model for this provider.");
				}
				if (status >= 400 && status < 500 && status != 429) {
					throw new ServiceException("AI provider '" + named.name() + "' rejected the request (HTTP " + status
							+ "): " + e.getResponseBody());
				}
				log.warn("AI call to '{}' attempt {}/{} failed: HTTP {}", named.name(), attempt, attempts, status);
				if (attempt < attempts) {
					sleepBeforeRetry(attempt);
				}
			} catch (Exception e) {
				log.warn("AI call to '{}' attempt {}/{} failed: {}", named.name(), attempt, attempts, e.getMessage());
				if (attempt < attempts) {
					sleepBeforeRetry(attempt);
				}
			}
		}
		throw new ServiceException(
				"AI provider '" + named.name() + "' is temporarily unavailable. Please try again later.");
	}

	private String callOpenAi(HttpClient client, AiProperties.Provider provider, String systemPrompt,
			String userPrompt) {
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("model", provider.getModel());
		body.put("temperature", 0);
		body.put("messages",
				List.of(Map.of("role", "system", "content", systemPrompt), Map.of("role", "user", "content", userPrompt)));
		if (provider.getMaxTokens() != null) {
			body.put("max_tokens", provider.getMaxTokens());
		}

		HttpRequest.Builder builder = newBuilder(provider, OPENAI_CHAT_ENDPOINT);
		if (StringUtils.hasText(provider.getApiKey())) {
			builder.header("Authorization", "Bearer " + provider.getApiKey());
		}
		JsonNode response = send(client, builder, body);
		return response.path("choices").path(0).path("message").path("content").asText(null);
	}

	private String callAnthropic(HttpClient client, String name, AiProperties.Provider provider, String systemPrompt,
			String userPrompt) {
		if (!StringUtils.hasText(provider.getApiKey())) {
			throw new ServiceException("AI provider '" + name + "' requires an api-key (anthropic protocol).");
		}
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("model", provider.getModel());
		body.put("max_tokens", provider.getMaxTokens() != null ? provider.getMaxTokens() : DEFAULT_MAX_TOKENS);
		body.put("system", systemPrompt);
		body.put("messages", List.of(Map.of("role", "user", "content", userPrompt)));

		HttpRequest.Builder builder = newBuilder(provider, ANTHROPIC_MESSAGES_ENDPOINT);
		builder.header("x-api-key", provider.getApiKey());
		builder.header("anthropic-version", ANTHROPIC_VERSION);
		JsonNode response = send(client, builder, body);
		return response.path("content").path(0).path("text").asText(null);
	}

	private HttpRequest.Builder newBuilder(AiProperties.Provider provider, String path) {
		if (!StringUtils.hasText(provider.getBaseUrl())) {
			throw new ServiceException("AI provider has no base-url configured.");
		}
		HttpRequest.Builder builder = HttpRequest.newBuilder()
				.uri(URI.create(provider.getBaseUrl() + path))
				.timeout(Duration.ofMillis(provider.getReadTimeoutMs()))
				.header("Content-Type", "application/json");
		provider.getExtraHeaders().forEach(builder::header);
		return builder;
	}

	private JsonNode send(HttpClient client, HttpRequest.Builder builder, Map<String, Object> body) {
		try {
			String json = objectMapper.writeValueAsString(body);
			HttpRequest request = builder.POST(HttpRequest.BodyPublishers.ofString(json)).build();
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			int status = response.statusCode();
			if (status >= 400) {
				throw new AiHttpException(status, response.body());
			}
			JsonNode node = objectMapper.readTree(response.body());
			return node != null ? node : objectMapper.createObjectNode();
		} catch (AiHttpException e) {
			throw e;
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new ServiceException("AI request interrupted.");
		} catch (Exception e) {
			throw new ServiceException("AI request failed: " + e.getMessage());
		}
	}

	private HttpClient clientFor(String providerName, AiProperties.Provider provider) {
		if (!StringUtils.hasText(provider.getBaseUrl())) {
			throw new ServiceException("AI provider '" + providerName + "' has no base-url configured.");
		}
		return clients.computeIfAbsent(providerName.toLowerCase(), key -> HttpClient.newBuilder()
				.connectTimeout(Duration.ofMillis(provider.getConnectTimeoutMs()))
				.version(HttpClient.Version.HTTP_2)
				.build());
	}

	private void sleepBeforeRetry(int attempt) {
		try {
			Thread.sleep(200L * attempt);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	private static class AiHttpException extends RuntimeException {
		private final int statusCode;
		private final String responseBody;

		AiHttpException(int statusCode, String responseBody) {
			this.statusCode = statusCode;
			this.responseBody = responseBody;
		}

		int getStatusCode() {
			return statusCode;
		}

		String getResponseBody() {
			return responseBody;
		}
	}
}
