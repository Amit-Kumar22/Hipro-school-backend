package com.hipro.school.backend.config;

import java.util.Map;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import com.hipro.school.backend.security.JwtService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketJwtAuthInterceptor implements ChannelInterceptor {

	private final JwtService jwtService;
	private final UserDetailsService userDetailsService;

	public static final String ATTR_USER_EMAIL = "userEmail";

	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

		if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
			String authHeader = accessor.getFirstNativeHeader("Authorization");

			if (authHeader != null && authHeader.startsWith("Bearer ")) {
				String jwt = authHeader.substring(7);
				try {
					String userEmail = jwtService.extractUsername(jwt);
					if (userEmail != null) {
						UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
						if (jwtService.isTokenValid(jwt, userDetails)) {
							accessor.getSessionAttributes().put(ATTR_USER_EMAIL, userEmail);
							log.debug("WebSocket authenticated for user: {}", userEmail);
						}
					}
				} catch (Exception e) {
					log.warn("WebSocket JWT authentication failed: {}", e.getMessage());
				}
			}
		}
		return message;
	}
}
