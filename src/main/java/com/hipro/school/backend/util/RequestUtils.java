package com.hipro.school.backend.util;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;


@Component
public class RequestUtils {

	private final HttpServletRequest request;

	public RequestUtils(HttpServletRequest request) {
		this.request = request;
	}

	public String getClientIpAddress() {
		String xfHeader = request.getHeader("X-Forwarded-For");
		if (xfHeader != null && !xfHeader.isEmpty()) {
			return xfHeader.split(",")[0];
		}
		return request.getRemoteAddr();
	}

	public String getUserAgent() {
		return request.getHeader("User-Agent");
	}

	public static String generate(HttpServletRequest request) {
		String raw = request.getRemoteAddr() + "|" + request.getHeader("User-Agent");
		return DigestUtils.sha256Hex(raw);
	}
}
