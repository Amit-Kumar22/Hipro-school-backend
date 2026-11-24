package com.hipro.school.backend.services.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.hipro.school.backend.dtos.ai.AiReportResult;
import com.hipro.school.backend.enums.AiIntent;

public interface AiIntentHandler {

	AiIntent intent();

	AiReportResult handle(JsonNode params, AiQueryContext context);
}
