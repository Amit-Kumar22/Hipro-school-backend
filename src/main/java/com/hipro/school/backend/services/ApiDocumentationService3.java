package com.hipro.school.backend.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ApiDocumentationService3 {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private ObjectMapper objectMapper;

	@Value("${api.documentation.url}")
	private String openApiUrl;

	@Value("${api.documentation.output:docs}")
	private String outputDirectory;

	/**
	 * Fetch OpenAPI JSON from SpringDoc
	 */
	public JsonNode getOpenApiDocument() {

		log.info("Fetching OpenAPI document from {}", openApiUrl);
		ResponseEntity<String> response = restTemplate.getForEntity(openApiUrl, String.class);

		try {
			return objectMapper.readTree(response.getBody());

		} catch (JsonProcessingException e) {
			log.error("Failed to parse OpenAPI documentation", e);
			throw new RuntimeException("Failed to parse OpenAPI documentation", e);
		}
	}

	/**
	 * Generate Markdown documentation
	 */
	public void generateDocumentation() {

		log.info("Generating API documentation into directory {}", outputDirectory);
		JsonNode openApi = getOpenApiDocument();

		JsonNode paths = openApi.get("paths");

		if (paths == null || !paths.fields().hasNext()) {
			throw new RuntimeException("No API paths found in OpenAPI document");
		}

		Path outputPath = Paths.get(outputDirectory);

		try {

			Files.createDirectories(outputPath);

			/*
			 * Group APIs by OpenAPI tag
			 *
			 * Example:
			 *
			 * User -> GET /users -> POST /users
			 *
			 * Hotel -> GET /hotels
			 */
			Map<String, StringBuilder> controllerDocs = new LinkedHashMap<>();

			Iterator<Map.Entry<String, JsonNode>> pathIterator = paths.fields();

			while (pathIterator.hasNext()) {

				Map.Entry<String, JsonNode> pathEntry = pathIterator.next();

				String path = pathEntry.getKey();

				JsonNode methods = pathEntry.getValue();

				Iterator<Map.Entry<String, JsonNode>> methodIterator = methods.fields();

				while (methodIterator.hasNext()) {

					Map.Entry<String, JsonNode> methodEntry = methodIterator.next();

					String method = methodEntry.getKey().toUpperCase();

					/*
					 * Ignore OpenAPI metadata
					 */
					if (!isHttpMethod(method)) {
						continue;
					}

					JsonNode operation = methodEntry.getValue();

					String tag = getTag(operation);

					controllerDocs.computeIfAbsent(tag, key -> new StringBuilder())
							.append(generateApiMarkdown(path, method, operation, openApi)).append("\n\n---\n\n");
				}
			}

			/*
			 * Generate individual Markdown files
			 */
			for (Map.Entry<String, StringBuilder> entry : controllerDocs.entrySet()) {

				String tag = entry.getKey();

				String fileName = sanitizeFileName(tag) + "Controller.md";

				Path file = outputPath.resolve(fileName);

				String content = "# " + tag + " API\n\n" + entry.getValue();

				Files.writeString(file, content);
			}

			/*
			 * Generate API_INDEX.md
			 */
			generateIndex(outputPath, controllerDocs);

		} catch (IOException e) {

			log.error("Failed to generate API documentation", e);
			throw new RuntimeException("Failed to generate API documentation", e);
		}
	}

	/**
	 * Get OpenAPI tag
	 */
	private String getTag(JsonNode operation) {

		JsonNode tags = operation.get("tags");

		if (tags != null && tags.isArray() && tags.size() > 0) {

			return tags.get(0).asText();
		}

		return "General";
	}

	/**
	 * Generate Markdown for one API
	 */
	private String generateApiMarkdown(String path, String method, JsonNode operation, JsonNode openApi) {

		StringBuilder md = new StringBuilder();

		String summary = getText(operation, "summary");

		String description = getText(operation, "description");

		md.append("## ").append(method).append(" ").append(path).append("\n\n");

		if (!summary.isBlank()) {

			md.append("### Summary\n\n").append(summary).append("\n\n");
		}

		if (!description.isBlank()) {

			md.append("### Description\n\n").append(description).append("\n\n");
		}

		/*
		 * Parameters
		 */
		JsonNode parameters = operation.get("parameters");

		if (parameters != null && parameters.isArray() && parameters.size() > 0) {

			md.append("### Parameters\n\n");

			md.append("| Name | Location | Type | Required |\n");
			md.append("|---|---|---|---|\n");

			for (JsonNode parameter : parameters) {

				String name = getText(parameter, "name");

				String location = getText(parameter, "in");

				boolean required = parameter.path("required").asBoolean(false);

				String type = parameter.path("schema").path("type").asText("object");

				md.append("| ").append(name).append(" | ").append(location).append(" | ").append(type).append(" | ")
						.append(required ? "Yes" : "No").append(" |\n");
			}

			md.append("\n");
		}

		/*
		 * Request Body
		 */
		JsonNode requestBody = operation.get("requestBody");

		if (requestBody != null) {

			JsonNode schema = extractSchemaFromContent(requestBody.get("content"));

			if (schema != null) {

				md.append("### Request Body\n\n");

				md.append("```json\n");

				md.append(generateExample(schema, openApi));

				md.append("\n```\n\n");
			}
		}

		/*
		 * Responses
		 */
		JsonNode responses = operation.get("responses");

		if (responses != null) {

			md.append("### Responses\n\n");

			Iterator<Map.Entry<String, JsonNode>> responseIterator = responses.fields();

			while (responseIterator.hasNext()) {

				Map.Entry<String, JsonNode> responseEntry = responseIterator.next();

				String statusCode = responseEntry.getKey();

				JsonNode response = responseEntry.getValue();

				md.append("#### ").append(statusCode).append(" - ").append(getText(response, "description"))
						.append("\n\n");

				JsonNode schema = extractSchemaFromContent(response.get("content"));

				if (schema != null) {

					md.append("```json\n");

					md.append(generateExample(schema, openApi));

					md.append("\n```\n\n");

				} else {

					md.append("_No response body._\n\n");
				}
			}
		}

		return md.toString();
	}

	/**
	 * Pull the "schema" node out of a "content" node, regardless of which
	 * media type key it's stored under. Springdoc frequently documents
	 * responses/request bodies under "*&#47;*" instead of "application/json"
	 * when the endpoint has no explicit produces/consumes, so we can't rely
	 * on "application/json" always being present.
	 */
	private JsonNode extractSchemaFromContent(JsonNode content) {

		if (content == null || !content.fields().hasNext()) {
			return null;
		}

		JsonNode mediaTypeNode = content.get("application/json");

		if (mediaTypeNode == null) {
			mediaTypeNode = content.get("*/*");
		}

		if (mediaTypeNode == null) {
			// fall back to whatever the first declared media type is
			mediaTypeNode = content.fields().next().getValue();
		}

		return mediaTypeNode.get("schema");
	}

	/**
	 * Generate example JSON from schema. Handles $ref, object, array and
	 * primitive schemas (previously only $ref was supported, which meant
	 * List<T> responses and inline schemas rendered as empty "{}").
	 */
	private String generateExample(JsonNode schema, JsonNode openApi) {

		Object resolved = resolveExampleValue(schema, openApi, 0);

		try {
			return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(resolved);

		} catch (JsonProcessingException e) {
			return "{}";
		}
	}

	/**
	 * Generate JSON object example
	 */
	private Map<String, Object> generateObjectExample(JsonNode schema, JsonNode openApi, int depth) {

		Map<String, Object> example = new LinkedHashMap<>();

		JsonNode properties = schema.get("properties");

		if (properties != null) {

			Iterator<Map.Entry<String, JsonNode>> fields = properties.fields();

			while (fields.hasNext()) {

				Map.Entry<String, JsonNode> field = fields.next();

				Object value = resolveExampleValue(field.getValue(), openApi, depth + 1);

				example.put(field.getKey(), value);
			}
		}

		return example;
	}

	/**
	 * Resolve any schema node ($ref, object, array, or primitive) into a
	 * sample Java value that Jackson can pretty-print. Recursion is depth
	 * limited to guard against self-referencing schemas.
	 */
	private Object resolveExampleValue(JsonNode schema, JsonNode openApi, int depth) {

		if (schema == null || depth > 6) {
			return null;
		}

		if (schema.has("example")) {
			return schema.get("example").asText();
		}

		if (schema.has("$ref")) {

			String ref = schema.get("$ref").asText();

			String schemaName = ref.substring(ref.lastIndexOf("/") + 1);

			JsonNode actualSchema = openApi.path("components").path("schemas").get(schemaName);

			if (actualSchema != null) {
				return resolveExampleValue(actualSchema, openApi, depth + 1);
			}

			return schemaName;
		}

		String type = schema.path("type").asText("object");

		return switch (type) {

		case "integer", "number" -> 0;

		case "boolean" -> false;

		case "array" -> new Object[] { resolveExampleValue(schema.get("items"), openApi, depth + 1) };

		case "object" -> generateObjectExample(schema, openApi, depth);

		default -> "string";
		};
	}

	/**
	 * Generate API_INDEX.md
	 */
	private void generateIndex(Path outputPath, Map<String, StringBuilder> controllerDocs) throws IOException {

		StringBuilder md = new StringBuilder();

		md.append("# API Documentation\n\n");

		md.append("Generated automatically from OpenAPI.\n\n");

		md.append("## Controllers\n\n");

		for (String tag : controllerDocs.keySet()) {

			String fileName = sanitizeFileName(tag) + ".md";

			md.append("- [").append(tag).append(" API](").append(fileName).append(")\n");
		}

		Files.writeString(outputPath.resolve("API_INDEX.md"), md.toString());
	}

	private boolean isHttpMethod(String method) {

		return switch (method) {

		case "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "HEAD" -> true;

		default -> false;
		};
	}

	private String getText(JsonNode node, String field) {

		if (node == null) {
			return "";
		}

		return node.path(field).asText("");
	}

	private String sanitizeFileName(String name) {

		return name.replaceAll("[^a-zA-Z0-9_-]", "");
	}
}