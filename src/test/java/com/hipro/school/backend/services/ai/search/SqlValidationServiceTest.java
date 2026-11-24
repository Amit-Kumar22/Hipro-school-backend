package com.hipro.school.backend.services.ai.search;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.hipro.school.backend.config.AiSearchProperties;
import com.hipro.school.backend.exceptions.ServiceException;

class SqlValidationServiceTest {

	private SqlValidationService validationService;

	@BeforeEach
	void setUp() {
		AiSearchProperties properties = new AiSearchProperties();
		properties.setApprovedTables(new LinkedHashSet<>(Set.of("students", "student_fee", "classes", "user")));
		properties.setMaxSqlLength(10000);
		validationService = new SqlValidationService(properties);
	}

	@Test
	@DisplayName("Should accept valid SELECT statement")
	void shouldAcceptValidSelect() {
		assertDoesNotThrow(() -> validationService.validate("SELECT * FROM students"));
	}

	@Test
	@DisplayName("Should accept SELECT with JOIN")
	void shouldAcceptSelectWithJoin() {
		assertDoesNotThrow(() -> validationService
				.validate("SELECT s.id, s.admission_number FROM students s JOIN classes c ON s.class_id = c.id"));
	}

	@Test
	@DisplayName("Should accept SELECT with WHERE clause")
	void shouldAcceptSelectWithWhere() {
		assertDoesNotThrow(() -> validationService.validate("SELECT * FROM student_fee WHERE status = 'PENDING'"));
	}

	@Test
	@DisplayName("Should accept SELECT with GROUP BY and aggregates")
	void shouldAcceptSelectWithGroupBy() {
		assertDoesNotThrow(() -> validationService
				.validate("SELECT status, COUNT(*) as count, SUM(amount) as total FROM student_fee GROUP BY status"));
	}

	@Test
	@DisplayName("Should accept SELECT with ORDER BY and LIMIT")
	void shouldAcceptSelectWithOrderByAndLimit() {
		assertDoesNotThrow(() -> validationService
				.validate("SELECT * FROM student_fee ORDER BY due_amount DESC LIMIT 10"));
	}

	@Test
	@DisplayName("Should accept SELECT with subquery")
	void shouldAcceptSelectWithSubquery() {
		assertDoesNotThrow(() -> validationService
				.validate("SELECT * FROM students WHERE id IN (SELECT student_id FROM student_fee WHERE status = 'PENDING')"));
	}

	@Test
	@DisplayName("Should reject INSERT statement")
	void shouldRejectInsert() {
		assertThrows(ServiceException.class, () -> validationService.validate("INSERT INTO students (id) VALUES (1)"));
	}

	@Test
	@DisplayName("Should reject UPDATE statement")
	void shouldRejectUpdate() {
		assertThrows(ServiceException.class, () -> validationService.validate("UPDATE students SET name = 'test'"));
	}

	@Test
	@DisplayName("Should reject DELETE statement")
	void shouldRejectDelete() {
		assertThrows(ServiceException.class, () -> validationService.validate("DELETE FROM students WHERE id = 1"));
	}

	@Test
	@DisplayName("Should reject DROP statement")
	void shouldRejectDrop() {
		assertThrows(ServiceException.class, () -> validationService.validate("DROP TABLE students"));
	}

	@Test
	@DisplayName("Should reject ALTER statement")
	void shouldRejectAlter() {
		assertThrows(ServiceException.class, () -> validationService.validate("ALTER TABLE students ADD COLUMN test VARCHAR(50)"));
	}

	@Test
	@DisplayName("Should reject CREATE statement")
	void shouldRejectCreate() {
		assertThrows(ServiceException.class, () -> validationService.validate("CREATE TABLE test (id INT)"));
	}

	@Test
	@DisplayName("Should reject TRUNCATE statement")
	void shouldRejectTruncate() {
		assertThrows(ServiceException.class, () -> validationService.validate("TRUNCATE TABLE students"));
	}

	@Test
	@DisplayName("Should reject multiple statements")
	void shouldRejectMultipleStatements() {
		assertThrows(ServiceException.class,
				() -> validationService.validate("SELECT * FROM students; SELECT * FROM classes"));
	}

	@Test
	@DisplayName("Should reject SQL with comments")
	void shouldRejectSqlWithComments() {
		assertThrows(ServiceException.class, () -> validationService.validate("SELECT * FROM students -- comment"));
		assertThrows(ServiceException.class, () -> validationService.validate("SELECT * FROM students /* comment */"));
	}

	@Test
	@DisplayName("Should reject empty SQL")
	void shouldRejectEmptySql() {
		assertThrows(ServiceException.class, () -> validationService.validate(""));
		assertThrows(ServiceException.class, () -> validationService.validate(null));
	}

	@Test
	@DisplayName("Should reject unauthorized table")
	void shouldRejectUnauthorizedTable() {
		assertThrows(ServiceException.class, () -> validationService.validate("SELECT * FROM unauthorized_table"));
	}

	@Test
	@DisplayName("Should reject system schema access")
	void shouldRejectSystemSchema() {
		assertThrows(ServiceException.class, () -> validationService.validate("SELECT * FROM information_schema.tables"));
		assertThrows(ServiceException.class, () -> validationService.validate("SELECT * FROM mysql.user"));
	}

	@Test
	@DisplayName("Should reject SQL exceeding max length")
	void shouldRejectSqlExceedingMaxLength() {
		AiSearchProperties properties = new AiSearchProperties();
		properties.setApprovedTables(new LinkedHashSet<>(Set.of("students")));
		properties.setMaxSqlLength(50);
		SqlValidationService service = new SqlValidationService(properties);

		String longSql = "SELECT * FROM students WHERE id = 1 AND name = 'very long name that exceeds limit'";
		assertThrows(ServiceException.class, () -> service.validate(longSql));
	}

	@Test
	@DisplayName("Should reject invalid SQL syntax")
	void shouldRejectInvalidSyntax() {
		assertThrows(ServiceException.class, () -> validationService.validate("SELECT FROM WHERE"));
	}

	@Test
	@DisplayName("Should reject GRANT statement")
	void shouldRejectGrant() {
		assertThrows(ServiceException.class, () -> validationService.validate("GRANT ALL ON *.* TO 'user'@'localhost'"));
	}

	@Test
	@DisplayName("Should reject REVOKE statement")
	void shouldRejectRevoke() {
		assertThrows(ServiceException.class, () -> validationService.validate("REVOKE ALL ON *.* FROM 'user'@'localhost'"));
	}

	@Test
	@DisplayName("Should reject CALL statement")
	void shouldRejectCall() {
		assertThrows(ServiceException.class, () -> validationService.validate("CALL sp_get_students()"));
	}

	@Test
	@DisplayName("Should reject INTO OUTFILE")
	void shouldRejectIntoOutfile() {
		assertThrows(ServiceException.class,
				() -> validationService.validate("SELECT * FROM students INTO OUTFILE '/tmp/data.csv'"));
	}

	@Test
	@DisplayName("Should accept SELECT with CTE (read-only)")
	void shouldAcceptSelectWithCte() {
		assertDoesNotThrow(() -> validationService
				.validate("WITH pending AS (SELECT * FROM student_fee WHERE status = 'PENDING') SELECT * FROM pending"));
	}

	@Test
	@DisplayName("Should accept SELECT with date functions")
	void shouldAcceptSelectWithDateFunctions() {
		assertDoesNotThrow(() -> validationService
				.validate("SELECT * FROM student_fee WHERE fee_date >= DATE_SUB(CURDATE(), INTERVAL 1 MONTH)"));
	}

	@Test
	@DisplayName("Should accept SELECT with CASE expression")
	void shouldAcceptSelectWithCase() {
		assertDoesNotThrow(() -> validationService
				.validate("SELECT id, CASE WHEN status = 'PAID' THEN 'Paid' ELSE 'Unpaid' END as fee_status FROM student_fee"));
	}
}
