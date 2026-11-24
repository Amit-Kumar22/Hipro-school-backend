package com.hipro.school.backend.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

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
public class ApiDocumentationServiceLatest {

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

					if (!isHttpMethod(method)) {
						continue;
					}

					JsonNode operation = methodEntry.getValue();

					String tag = getTag(operation);

					controllerDocs.computeIfAbsent(tag, key -> new StringBuilder())
							.append(generateApiMarkdown(path, method, operation, openApi)).append("\n\n---\n\n");
				}
			}

			for (Map.Entry<String, StringBuilder> entry : controllerDocs.entrySet()) {

				String tag = entry.getKey();

				String fileName = sanitizeFileName(tag) + "Controller.md";

				Path file = outputPath.resolve(fileName);

				String content = "# " + tag + " API\n\n" + entry.getValue();

				Files.writeString(file, content);
			}

			generateIndex(outputPath, controllerDocs);

		} catch (IOException e) {

			log.error("Failed to generate API documentation", e);
			throw new RuntimeException("Failed to generate API documentation", e);
		}
	}

	private String getTag(JsonNode operation) {

		JsonNode tags = operation.get("tags");

		if (tags != null && tags.isArray() && tags.size() > 0) {
			return tags.get(0).asText();
		}

		return "General";
	}

	/**
	 * Generate Markdown for one API operation
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

		appendParameters(md, operation, openApi);

		/*
		 * Request Body — flexible: works whether the body resolves to a
		 * $ref'd entity, an inline object, an array, or a primitive.
		 */
		JsonNode requestBody = operation.get("requestBody");

		if (requestBody != null) {

			JsonNode schema = extractSchemaFromContent(requestBody.get("content"));

			if (schema != null) {

				md.append("### Request Body\n\n");

				md.append("**Shape:** ").append(describeShape(schema, openApi, 0)).append("\n\n");

				md.append("```json\n");
				md.append(generateExample(schema, openApi));
				md.append("\n```\n\n");
			}
		}

		/*
		 * Responses — flexible: works whether the response resolves to
		 * ApiResponse<T> (wrapped), a bare List/array, a Page-style object,
		 * a raw entity, or a primitive (e.g. plain String / JsonNode).
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

					md.append("**Shape:** ").append(describeShape(schema, openApi, 0)).append("\n\n");

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
	 * Renders the parameters table. Handles both simple parameters (schema
	 * has a "type" directly) and parameters whose schema is a $ref or an
	 * inline object with properties (e.g. a Pageable-style parameter object)
	 * by expanding each property into its own row instead of collapsing it
	 * to a single opaque "object" row.
	 */
	private void appendParameters(StringBuilder md, JsonNode operation, JsonNode openApi) {

		JsonNode parameters = operation.get("parameters");

		if (parameters == null || !parameters.isArray() || parameters.size() == 0) {
			return;
		}

		md.append("### Parameters\n\n");
		md.append("| Name | Location | Type | Required |\n");
		md.append("|---|---|---|---|\n");

		for (JsonNode parameter : parameters) {

			String name = getText(parameter, "name");
			String location = getText(parameter, "in");
			boolean required = parameter.path("required").asBoolean(false);

			JsonNode schema = parameter.get("schema");
			JsonNode resolvedSchema = resolveSchemaNode(schema, openApi, 0);

			String type = resolvedSchema != null ? resolvedSchema.path("type").asText("object") : "object";

			md.append("| ").append(name).append(" | ").append(location).append(" | ").append(type).append(" | ")
					.append(required ? "Yes" : "No").append(" |\n");

			// If the parameter resolves to an object with known properties
			// (e.g. a @ParameterObject-style composite param), expand its
			// fields as sub-rows so they're not left opaque.
			if (resolvedSchema != null && "object".equals(type) && resolvedSchema.has("properties")) {

				Iterator<Map.Entry<String, JsonNode>> props = resolvedSchema.get("properties").fields();

				while (props.hasNext()) {

					Map.Entry<String, JsonNode> prop = props.next();

					String propType = prop.getValue().path("type").asText("object");

					md.append("| ").append(name).append(".").append(prop.getKey()).append(" | ").append(location)
							.append(" | ").append(propType).append(" | No |\n");
				}
			}
		}

		md.append("\n");
	}

	/**
	 * Pull the "schema" node out of a "content" node, regardless of which
	 * media type key it's stored under. Springdoc frequently documents
	 * responses/request bodies under "*&#47;*" instead of "application/json"
	 * when the endpoint has no explicit produces/consumes.
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
			mediaTypeNode = content.fields().next().getValue();
		}

		return mediaTypeNode.get("schema");
	}

	/**
	 * Resolves a schema node through $ref (if present), returning the
	 * actual schema definition. Depth limited to guard against cycles.
	 */
	private JsonNode resolveSchemaNode(JsonNode schema, JsonNode openApi, int depth) {

		if (schema == null || depth > 6) {
			return schema;
		}

		if (schema.has("$ref")) {

			String ref = schema.get("$ref").asText();
			String schemaName = ref.substring(ref.lastIndexOf("/") + 1);
			JsonNode actualSchema = openApi.path("components").path("schemas").get(schemaName);

			if (actualSchema != null) {
				return resolveSchemaNode(actualSchema, openApi, depth + 1);
			}

			return schema;
		}

		return schema;
	}

	/**
	 * Produces a short human/AI-readable label describing what shape a
	 * response or request body actually is, so consumers of this doc don't
	 * have to guess whether they're dealing with a wrapped ApiResponse<T>,
	 * a raw list, a paginated page, a raw entity, or a primitive value.
	 */
	private String describeShape(JsonNode schema, JsonNode openApi, int depth) {

		if (schema == null || depth > 6) {
			return "unknown";
		}

		JsonNode resolved = resolveSchemaNode(schema, openApi, 0);

		if (resolved == null) {
			return "unknown";
		}

		String type = resolved.path("type").asText("object");

		if ("array".equals(type)) {
			JsonNode items = resolved.get("items");
			return "Array / List of " + describeShape(items, openApi, depth + 1);
		}

		if (!"object".equals(type)) {
			return "Primitive value (" + type + ")";
		}

		JsonNode properties = resolved.get("properties");

		if (properties == null) {
			return "Raw entity object";
		}

		Set<String> fieldNames = new LinkedHashSet<>();
		properties.fieldNames().forEachRemaining(fieldNames::add);

		if (fieldNames.contains("statusCode") && fieldNames.contains("success") && fieldNames.contains("message")
				&& fieldNames.contains("result")) {

			JsonNode resultSchema = properties.get("result");
			String innerShape = describeShape(resultSchema, openApi, depth + 1);

			return "Wrapped in ApiResponse<T> — result is: " + innerShape;
		}

		if (fieldNames.contains("content") && fieldNames.contains("totalElements")
				&& fieldNames.contains("totalPages")) {

			JsonNode contentSchema = properties.get("content");
			String innerShape = describeShape(contentSchema, openApi, depth + 1);

			return "Paginated page object (Page<T>-style) — content is: " + innerShape;
		}

		return "Raw entity object";
	}

	/**
	 * Generate example JSON from schema. Handles $ref, object, array and
	 * primitive schemas so it works uniformly whether the top-level shape
	 * is a wrapped ApiResponse<T>, a bare list, a page object, or a raw
	 * entity/primitive.
	 */
	private String generateExample(JsonNode schema, JsonNode openApi) {

		Object resolved = resolveExampleValue(schema, openApi, 0);

		try {
			return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(resolved);

		} catch (JsonProcessingException e) {
			return "{}";
		}
	}

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
	 * sample Java value that Jackson can pretty-print with the CORRECT
	 * JSON type — e.g. a declared "integer" schema always yields a number,
	 * a "boolean" schema always yields true/false, even if the underlying
	 * @Schema(example = "...") annotation on the Java field stored the
	 * example as a String (a Swagger-annotation limitation, not a
	 * reflection of the real field type).
	 */
	private Object resolveExampleValue(JsonNode schema, JsonNode openApi, int depth) {

		if (schema == null || depth > 6) {
			return null;
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
		String format = schema.path("format").asText("");

		// If the schema declares an example, coerce it to the DECLARED type
		// instead of trusting whatever raw type the annotation stored it as.
		if (schema.has("example")) {
			return coerceToType(schema.get("example"), type);
		}

		// Enums: use the first declared value as the example.
		if (schema.has("enum") && schema.get("enum").isArray() && schema.get("enum").size() > 0) {
			return schema.get("enum").get(0).asText();
		}

		return switch (type) {

		case "integer" -> "int64".equals(format) ? 0L : 0;

		case "number" -> 0.0;

		case "boolean" -> false;

		case "array" -> new Object[] { resolveExampleValue(schema.get("items"), openApi, depth + 1) };

		case "object" -> generateObjectExample(schema, openApi, depth);

		case "string" -> switch (format) {
			case "date" -> "2026-01-01";
			case "date-time" -> "2026-01-01T00:00:00Z";
			case "uuid" -> "00000000-0000-0000-0000-000000000000";
			case "email" -> "user@example.com";
			default -> "string";
		};

		default -> "string";
		};
	}

	/**
	 * Coerces a raw example JsonNode (which Swagger annotations always
	 * store loosely) into the type the schema actually declares.
	 */
	private Object coerceToType(JsonNode exampleNode, String declaredType) {

		String raw = exampleNode.asText();

		try {
			return switch (declaredType) {

			case "integer" -> Long.parseLong(raw.trim());

			case "number" -> Double.parseDouble(raw.trim());

			case "boolean" -> Boolean.parseBoolean(raw.trim());

			default -> raw;
			};

		} catch (NumberFormatException e) {
			// Example value didn't actually match the declared type —
			// fall back to a safe default rather than propagating a bad string.
			return switch (declaredType) {
			case "integer" -> 0;
			case "number" -> 0.0;
			case "boolean" -> false;
			default -> raw;
			};
		}
	}

	private void generateIndex(Path outputPath, Map<String, StringBuilder> controllerDocs) throws IOException {

		StringBuilder md = new StringBuilder();

		md.append("# API Documentation\n\n");
		md.append("Generated automatically from OpenAPI.\n\n");
		md.append("## Controllers\n\n");

		for (String tag : controllerDocs.keySet()) {

			String fileName = sanitizeFileName(tag) + "Controller.md";

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