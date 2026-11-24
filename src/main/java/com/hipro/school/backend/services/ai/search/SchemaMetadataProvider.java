package com.hipro.school.backend.services.ai.search;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.springframework.stereotype.Component;

import com.hipro.school.backend.config.AiSearchProperties;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SchemaMetadataProvider {

	private static final Set<String> SYSTEM_SCHEMAS = Set.of("information_schema", "mysql", "performance_schema",
			"sys");

	private final DataSource dataSource;
	private final AiSearchProperties properties;

	public String getSchemaDescription() {
		try (Connection conn = dataSource.getConnection()) {
			DatabaseMetaData metaData = conn.getMetaData();
			String catalog = conn.getCatalog();

			List<String> tableNames = getApprovedTableNames(metaData, catalog);
			if (tableNames.isEmpty()) {
				return "No approved tables found in the database.";
			}

			Map<String, List<ColumnInfo>> tableColumns = new LinkedHashMap<>();
			Map<String, List<String>> tablePrimaryKeys = new LinkedHashMap<>();
			Map<String, List<ForeignKeyInfo>> tableForeignKeys = new LinkedHashMap<>();

			for (String tableName : tableNames) {
				tableColumns.put(tableName, getColumns(metaData, catalog, tableName));
				tablePrimaryKeys.put(tableName, getPrimaryKeys(metaData, catalog, tableName));
				tableForeignKeys.put(tableName, getForeignKeys(metaData, catalog, tableName));
			}

			return buildSchemaDescription(tableColumns, tablePrimaryKeys, tableForeignKeys);
		} catch (SQLException e) {
			log.error("Failed to read database schema metadata", e);
			throw new com.hipro.school.backend.exceptions.ServiceException(
					"Unable to read database schema. Please try again later.");
		}
	}

	public Map<String, List<String>> getApprovedColumnsByTable() {
		Map<String, List<String>> result = new LinkedHashMap<>();
		try (Connection conn = dataSource.getConnection()) {
			DatabaseMetaData metaData = conn.getMetaData();
			String catalog = conn.getCatalog();
			List<String> tableNames = getApprovedTableNames(metaData, catalog);
			for (String tableName : tableNames) {
				List<String> columns = new ArrayList<>();
				for (ColumnInfo col : getColumns(metaData, catalog, tableName)) {
					if (!properties.isColumnDenied(col.name())) {
						columns.add(col.name());
					}
				}
				result.put(tableName, columns);
			}
		} catch (SQLException e) {
			log.error("Failed to read column metadata", e);
		}
		return result;
	}

	private List<String> getApprovedTableNames(DatabaseMetaData metaData, String catalog) throws SQLException {
		List<String> approved = new ArrayList<>();
		try (ResultSet rs = metaData.getTables(catalog, null, "%", new String[] { "TABLE" })) {
			while (rs.next()) {
				String tableName = rs.getString("TABLE_NAME");
				String schema = rs.getString("TABLE_SCHEM");
				if (schema != null && SYSTEM_SCHEMAS.contains(schema.toLowerCase())) {
					continue;
				}
				if (properties.isTableApproved(tableName)) {
					approved.add(tableName);
				}
			}
		}
		return approved;
	}

	private List<ColumnInfo> getColumns(DatabaseMetaData metaData, String catalog, String tableName)
			throws SQLException {
		List<ColumnInfo> columns = new ArrayList<>();
		try (ResultSet rs = metaData.getColumns(catalog, null, tableName, "%")) {
			while (rs.next()) {
				String colName = rs.getString("COLUMN_NAME");
				String colType = rs.getString("TYPE_NAME");
				int nullable = rs.getInt("NULLABLE");
				columns.add(new ColumnInfo(colName, colType, nullable == DatabaseMetaData.columnNullable));
			}
		}
		return columns;
	}

	private List<String> getPrimaryKeys(DatabaseMetaData metaData, String catalog, String tableName)
			throws SQLException {
		List<String> pks = new ArrayList<>();
		try (ResultSet rs = metaData.getPrimaryKeys(catalog, null, tableName)) {
			while (rs.next()) {
				pks.add(rs.getString("COLUMN_NAME"));
			}
		}
		return pks;
	}

	private List<ForeignKeyInfo> getForeignKeys(DatabaseMetaData metaData, String catalog, String tableName)
			throws SQLException {
		List<ForeignKeyInfo> fks = new ArrayList<>();
		try (ResultSet rs = metaData.getImportedKeys(catalog, null, tableName)) {
			while (rs.next()) {
				String fkColumn = rs.getString("FKCOLUMN_NAME");
				String pkTable = rs.getString("PKTABLE_NAME");
				String pkColumn = rs.getString("PKCOLUMN_NAME");
				fks.add(new ForeignKeyInfo(fkColumn, pkTable, pkColumn));
			}
		}
		return fks;
	}

	private String buildSchemaDescription(Map<String, List<ColumnInfo>> tableColumns,
			Map<String, List<String>> tablePrimaryKeys, Map<String, List<ForeignKeyInfo>> tableForeignKeys) {
		StringBuilder sb = new StringBuilder();
		sb.append("MySQL Database Schema:\n\n");

		for (Map.Entry<String, List<ColumnInfo>> entry : tableColumns.entrySet()) {
			String tableName = entry.getKey();
			sb.append("Table: ").append(tableName).append("\n");

			List<String> pks = tablePrimaryKeys.getOrDefault(tableName, List.of());
			List<ForeignKeyInfo> fks = tableForeignKeys.getOrDefault(tableName, List.of());

			sb.append("Columns:\n");
			for (ColumnInfo col : entry.getValue()) {
				if (properties.isColumnDenied(col.name())) {
					continue;
				}
				sb.append("  - ").append(col.name()).append(" (").append(col.type());
				if (pks.contains(col.name())) {
					sb.append(", PRIMARY KEY");
				}
				for (ForeignKeyInfo fk : fks) {
					if (fk.columnName().equals(col.name())) {
						sb.append(", FOREIGN KEY -> ").append(fk.referencedTable()).append(".")
								.append(fk.referencedColumn());
					}
				}
				sb.append(col.nullable() ? ", nullable" : ", NOT NULL").append(")\n");
			}
			sb.append("\n");
		}

		return sb.toString();
	}

	private record ColumnInfo(String name, String type, boolean nullable) {
	}

	private record ForeignKeyInfo(String columnName, String referencedTable, String referencedColumn) {
	}
}
