package com.hipro.school.backend.config;

import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "application.ai.search")
public class AiSearchProperties {

	private boolean enabled = true;

	private int maxRows = 1000;

	private int queryTimeoutSeconds = 30;

	private int maxSqlLength = 10000;

	private Set<String> approvedTables = new LinkedHashSet<>();

	private Set<String> deniedColumns = new LinkedHashSet<>(Set.of("password", "otp", "raw_password", "bank_response",
			"refund_details", "razorpay_order_id", "transaction_id"));

	public boolean isTableApproved(String tableName) {
		return approvedTables.isEmpty() || approvedTables.contains(tableName.toLowerCase());
	}

	public boolean isColumnDenied(String columnName) {
		return deniedColumns.contains(columnName.toLowerCase());
	}
}
