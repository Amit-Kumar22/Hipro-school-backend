package com.hipro.school.backend.services.ai.search;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.hipro.school.backend.config.AiSearchProperties;
import com.hipro.school.backend.exceptions.ServiceException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReadOnlyQueryExecutor {

	private final DataSource dataSource;
	private final AiSearchProperties properties;

	public ReadOnlyResultSet execute(String sql) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.setMaxRows(properties.getMaxRows());
		jdbcTemplate.setQueryTimeout(properties.getQueryTimeoutSeconds());

		try {
			List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
			List<String> columns = rows.isEmpty() ? extractColumns(jdbcTemplate, sql)
					: new ArrayList<>(rows.get(0).keySet());

			List<Map<String, Object>> sanitizedRows = new ArrayList<>();
			for (Map<String, Object> row : rows) {
				Map<String, Object> sanitized = new LinkedHashMap<>();
				for (Map.Entry<String, Object> entry : row.entrySet()) {
					String colName = entry.getKey();
					if (properties.isColumnDenied(colName)) {
						continue;
					}
					sanitized.put(colName, sanitizeValue(entry.getValue()));
				}
				sanitizedRows.add(sanitized);
			}

			if (!sanitizedRows.isEmpty()) {
				columns = List.copyOf(sanitizedRows.get(0).keySet());
			}

			return new ReadOnlyResultSet(columns, sanitizedRows, sanitizedRows.size());
		} catch (org.springframework.dao.QueryTimeoutException e) {
			log.warn("Query timed out: {}", e.getMessage());
			throw new ServiceException("Query execution timed out. Please try a more specific query.");
		} catch (org.springframework.dao.DataAccessException e) {
			log.error("Query execution failed: {}", e.getMessage());
			throw new ServiceException("Failed to execute query. Please check your question and try again.");
		}
	}

	private List<String> extractColumns(JdbcTemplate jdbcTemplate, String sql) {
		try {
			return jdbcTemplate.execute((org.springframework.jdbc.core.ConnectionCallback<List<String>>) conn -> {
				try (Statement stmt = conn.createStatement()) {
					stmt.setMaxRows(1);
					try (ResultSet rs = stmt.executeQuery(sql)) {
						ResultSetMetaData metaData = rs.getMetaData();
						List<String> cols = new ArrayList<>();
						for (int i = 1; i <= metaData.getColumnCount(); i++) {
							String colName = metaData.getColumnLabel(i);
							if (!properties.isColumnDenied(colName)) {
								cols.add(colName);
							}
						}
						return cols;
					}
				}
			});
		} catch (org.springframework.dao.DataAccessException e) {
			log.warn("Failed to extract columns from query: {}", e.getMessage());
			return List.of();
		}
	}

	private Object sanitizeValue(Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof BigDecimal bd) {
			return bd.stripTrailingZeros().toPlainString();
		}
		if (value instanceof LocalDateTime ldt) {
			return ldt.toString();
		}
		if (value instanceof LocalDate ld) {
			return ld.toString();
		}
		if (value instanceof java.sql.Date date) {
			return date.toLocalDate().toString();
		}
		if (value instanceof java.sql.Timestamp ts) {
			return ts.toLocalDateTime().toString();
		}
		if (value instanceof byte[]) {
			return "[BINARY DATA]";
		}
		return value;
	}

	public record ReadOnlyResultSet(List<String> columns, List<Map<String, Object>> rows, int rowCount) {
	}
}
