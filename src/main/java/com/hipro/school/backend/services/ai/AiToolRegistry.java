package com.hipro.school.backend.services.ai;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.hipro.school.backend.enums.AiIntent;
import com.hipro.school.backend.exceptions.ServiceException;

@Component
public class AiToolRegistry {

	private final Map<AiIntent, AiIntentHandler> handlers;

	public AiToolRegistry(List<AiIntentHandler> handlerList) {
		this.handlers = handlerList.stream().collect(Collectors.toUnmodifiableMap(AiIntentHandler::intent,
				Function.identity(), (existing, duplicate) -> existing));
	}

	public AiIntentHandler handler(AiIntent intent) {
		AiIntentHandler handler = handlers.get(intent);
		if (handler == null) {
			throw new ServiceException("No report available for intent: " + intent);
		}
		return handler;
	}
}
