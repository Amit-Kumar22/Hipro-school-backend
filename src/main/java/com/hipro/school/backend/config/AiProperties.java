package com.hipro.school.backend.config;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.hipro.school.backend.exceptions.ServiceException;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "application.ai")
public class AiProperties {

	public enum Protocol {
		OPENAI, ANTHROPIC
	}

	public record NamedProvider(String name, Provider provider) {
	}

	private String defaultProvider;

	private boolean summaryEnabled = true;

	private Map<String, Provider> providers = new LinkedHashMap<>();

	public NamedProvider resolve(String providerName) {
		String name = (providerName == null || providerName.isBlank()) ? defaultProvider : providerName.trim();
		if (name == null || name.isBlank()) {
			throw new ServiceException(
					"No AI provider selected. Set application.ai.default-provider or pass a provider in the request.");
		}
		for (Map.Entry<String, Provider> entry : providers.entrySet()) {
			if (entry.getKey().equalsIgnoreCase(name)) {
				return new NamedProvider(entry.getKey(), entry.getValue());
			}
		}
		throw new ServiceException("Unknown AI provider '" + name + "'. Available providers: " + providers.keySet());
	}

	@Getter
	@Setter
	public static class Provider {

		private String baseUrl;

		// Optional: leave blank for local LLMs (Ollama, LM Studio, llama.cpp) that need no key
		private String apiKey;

		private String model;

		private Protocol protocol = Protocol.OPENAI;

		private int connectTimeoutMs = 5000;

		private int readTimeoutMs = 30000;

		private int maxAttempts = 2;

		// Optional output token cap. Null = provider default (2048 for anthropic)
		private Integer maxTokens;

		// Optional extra headers for gateways that need custom auth (e.g. x-goog-api-key)
		private Map<String, String> extraHeaders = new LinkedHashMap<>();
	}
}
