package com.hipro.school.backend.services.ai.search;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.hipro.school.backend.config.AiSearchProperties;
import com.hipro.school.backend.exceptions.ServiceException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.util.TablesNamesFinder;

@Slf4j
@Component
@RequiredArgsConstructor
public class SqlValidationService {

	private static final Set<String> FORBIDDEN_KEYWORDS = Set.of("INSERT", "UPDATE", "DELETE", "DROP", "ALTER",
			"CREATE", "TRUNCATE", "REPLACE", "GRANT", "REVOKE", "CALL", "EXEC", "EXECUTE", "INTO OUTFILE",
			"INTO DUMPFILE", "LOAD_FILE", "BENCHMARK", "SLEEP", "LOCK", "UNLOCK", "RESET", "FLUSH", "KILL",
			"SHUTDOWN", "PURGE", "ANALYZE", "CHECK", "OPTIMIZE", "REPAIR", "INSTALL", "UNINSTALL", "DEALLOCATE",
			"PREPARE", "HANDLER", "DO", "HELP", "USE", "START", "BEGIN", "COMMIT", "ROLLBACK", "SAVEPOINT",
			"RELEASE", "SET", "SHOW", "DESCRIBE", "EXPLAIN");

	private final AiSearchProperties properties;

	public void validate(String sql) {
		if (sql == null || sql.isBlank()) {
			throw new ServiceException("Generated SQL is empty.");
		}
		if (sql.length() > properties.getMaxSqlLength()) {
			throw new ServiceException("Generated SQL exceeds maximum allowed length.");
		}

		String trimmed = sql.trim();
		if (trimmed.contains(";")) {
			throw new ServiceException("Multiple SQL statements are not allowed.");
		}
		if (trimmed.contains("--") || trimmed.contains("/*") || trimmed.contains("*/") || trimmed.contains("#")) {
			throw new ServiceException("SQL comments are not allowed.");
		}

		String upperSql = trimmed.toUpperCase();
		for (String keyword : FORBIDDEN_KEYWORDS) {
			Pattern pattern = Pattern.compile("\\b" + Pattern.quote(keyword) + "\\b", Pattern.CASE_INSENSITIVE);
			if (pattern.matcher(trimmed).find()) {
				throw new ServiceException("SQL contains forbidden keyword: " + keyword);
			}
		}

		Statement statement;
		try {
			statement = CCJSqlParserUtil.parse(trimmed);
		} catch (JSQLParserException e) {
			throw new ServiceException("Invalid SQL syntax: " + e.getMessage());
		}

		if (!(statement instanceof Select)) {
			throw new ServiceException("Only SELECT statements are allowed.");
		}

		Select select = (Select) statement;
		TablesNamesFinder finder = new TablesNamesFinder();
		List<String> tables = finder.getTableList((net.sf.jsqlparser.statement.Statement) select);

		if (tables.isEmpty()) {
			throw new ServiceException("No tables referenced in the query.");
		}

		for (String table : tables) {
			String tableName = table.toLowerCase();
			if (tableName.contains("information_schema") || tableName.contains("mysql")
					|| tableName.contains("performance_schema") || tableName.contains("sys")) {
				throw new ServiceException("Access to system schema is not allowed.");
			}
			if (!properties.isTableApproved(tableName)) {
				throw new ServiceException("Access to table '" + tableName + "' is not permitted.");
			}
		}

		log.debug("SQL validation passed for tables: {}", tables);
	}
}
