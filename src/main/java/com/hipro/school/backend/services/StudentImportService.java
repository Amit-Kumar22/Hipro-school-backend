package com.hipro.school.backend.services;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.hipro.school.backend.dtos.StudentImportRow;
import com.hipro.school.backend.dtos.StudentRequest;
import com.hipro.school.backend.dtos.UploadResponse;
import com.hipro.school.backend.entities.SchoolClass;
import com.hipro.school.backend.enums.Gender;
import com.hipro.school.backend.exceptions.ServiceException;
import com.hipro.school.backend.repositories.SchoolClassRepository;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class StudentImportService {

	private static final Set<String> SUPPORTED_EXTENSIONS = Set.of("csv", "xls", "xlsx");

	private static final List<String> REQUIRED_COLUMNS = List.of("name", "parentPhone", "className");

	private static final int MAX_ERRORS = 100;

	private static final Map<String, String> HEADER_ALIASES = new LinkedHashMap<>();

	private static final List<DateTimeFormatter> DATE_FORMATTERS = List.of(DateTimeFormatter.ofPattern("yyyy-MM-dd"),
			DateTimeFormatter.ofPattern("dd/MM/yyyy"), DateTimeFormatter.ofPattern("d/M/yyyy"),
			DateTimeFormatter.ofPattern("dd-MM-yyyy"), DateTimeFormatter.ofPattern("dd-MMM-yyyy"));

	static {
		HEADER_ALIASES.put("name", "name");
		HEADER_ALIASES.put("studentname", "name");
		HEADER_ALIASES.put("fathername", "fatherName");
		HEADER_ALIASES.put("father", "fatherName");
		HEADER_ALIASES.put("mothername", "motherName");
		HEADER_ALIASES.put("mother", "motherName");
		HEADER_ALIASES.put("parentemail", "parentEmail");
		HEADER_ALIASES.put("email", "parentEmail");
		HEADER_ALIASES.put("parentphone", "parentPhone");
		HEADER_ALIASES.put("phone", "parentPhone");
		HEADER_ALIASES.put("mobileno", "parentPhone");
		HEADER_ALIASES.put("phoneno", "parentPhone");
		HEADER_ALIASES.put("classname", "className");
		HEADER_ALIASES.put("class", "className");
		HEADER_ALIASES.put("dob", "dob");
		HEADER_ALIASES.put("dateofbirth", "dob");
		HEADER_ALIASES.put("birthdate", "dob");
		HEADER_ALIASES.put("address", "address");
		HEADER_ALIASES.put("pincode", "pincode");
		HEADER_ALIASES.put("zipcode", "pincode");
		HEADER_ALIASES.put("postalcode", "pincode");
		HEADER_ALIASES.put("gender", "gender");
		HEADER_ALIASES.put("sex", "gender");
	}

	@Autowired
	private ProfileService profileService;

	@Autowired
	private SchoolClassRepository classRepository;

	@Autowired
	private Validator validator;

	public UploadResponse importStudents(MultipartFile file) {
		log.info(">> importStudents(file={})", file == null ? null : file.getOriginalFilename());

		if (file == null || file.isEmpty()) {
			throw new ServiceException("Please upload a non-empty CSV or Excel file.");
		}

		String extension = resolveExtension(file.getOriginalFilename());
		if (!SUPPORTED_EXTENSIONS.contains(extension)) {
			throw new ServiceException(
					"Unsupported file type: " + extension + ". Supported types: csv, xls, xlsx");
		}

		List<ParsedRow> rows;
		try {
			rows = "csv".equals(extension) ? readCsv(file) : readExcel(file);
		} catch (IOException e) {
			throw new ServiceException("Unable to read the uploaded file: " + e.getMessage());
		}

		List<String> errors = new ArrayList<>();
		int success = 0;

		for (ParsedRow parsedRow : rows) {
			try {
				StudentImportRow row = toRow(parsedRow.values());

				Set<ConstraintViolation<StudentImportRow>> violations = validator.validate(row);
				if (!violations.isEmpty()) {
					addError(errors, "Row " + parsedRow.rowNumber + ": " + violations.stream()
							.map(v -> v.getPropertyPath() + " " + v.getMessage()).collect(Collectors.joining(", ")));
					continue;
				}

				SchoolClass schoolClass = classRepository.findByClassNameIgnoreCase(row.getClassName())
						.orElseThrow(() -> new ServiceException(
								"Class not found with name: " + row.getClassName()));

				profileService.addNewStudent(toRequest(row, schoolClass));
				success++;
			} catch (Exception e) {
				addError(errors, "Row " + parsedRow.rowNumber + ": " + rootMessage(e));
			}
		}

		int failed = rows.size() - success;
		log.info("Import finished: total={}, success={}, failed={}", rows.size(), success, failed);
		return UploadResponse.builder().total(rows.size()).success(success).failed(failed).errors(errors).build();
	}

	// ------------------------- file reading -------------------------

	private List<ParsedRow> readCsv(MultipartFile file) throws IOException {
		String content = new String(file.getBytes(), StandardCharsets.UTF_8);
		if (content.startsWith("\uFEFF")) {
			content = content.substring(1);
		}

		List<String[]> records = splitCsv(content);
		if (records.isEmpty()) {
			throw new ServiceException("The uploaded file is empty.");
		}

		Map<String, Integer> headerIndex = mapHeaders(records.get(0));
		List<ParsedRow> rows = new ArrayList<>();
		for (int i = 1; i < records.size(); i++) {
			String[] cells = records.get(i);
			if (isBlankRow(cells)) {
				continue;
			}
			Map<String, Object> values = new LinkedHashMap<>();
			for (Map.Entry<String, Integer> entry : headerIndex.entrySet()) {
				int column = entry.getValue();
				values.put(entry.getKey(), column < cells.length ? cells[column] : null);
			}
			rows.add(new ParsedRow(i + 1, values));
		}

		if (rows.isEmpty()) {
			throw new ServiceException("No data rows found below the header row.");
		}
		return rows;
	}

	private List<ParsedRow> readExcel(MultipartFile file) throws IOException {
		List<ParsedRow> rows = new ArrayList<>();
		try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
			Sheet sheet = workbook.getSheetAt(0);
			Row headerRow = sheet.getRow(sheet.getFirstRowNum());
			if (headerRow == null) {
				throw new ServiceException("The uploaded file is empty.");
			}

			List<String> headers = new ArrayList<>();
			for (Cell cell : headerRow) {
				headers.add(stringValue(cell));
			}
			Map<String, Integer> headerIndex = mapHeaders(headers.toArray(new String[0]));

			for (int r = headerRow.getRowNum() + 1; r <= sheet.getLastRowNum(); r++) {
				Row row = sheet.getRow(r);
				if (row == null) {
					continue;
				}
				Map<String, Object> values = new LinkedHashMap<>();
				boolean hasValue = false;
				for (Map.Entry<String, Integer> entry : headerIndex.entrySet()) {
					Cell cell = row.getCell(entry.getValue());
					Object value = cellValue(cell);
					if (value != null && !value.toString().isBlank()) {
						hasValue = true;
					}
					values.put(entry.getKey(), value);
				}
				if (!hasValue) {
					continue;
				}
				rows.add(new ParsedRow(r + 1, values));
			}
		} catch (ServiceException e) {
			throw e;
		} catch (IOException | RuntimeException e) {
			throw new ServiceException("Unable to read the Excel file: " + e.getMessage());
		}

		if (rows.isEmpty()) {
			throw new ServiceException("No data rows found below the header row.");
		}
		return rows;
	}

	private List<String[]> splitCsv(String content) {
		List<String[]> records = new ArrayList<>();
		List<String> fields = new ArrayList<>();
		StringBuilder field = new StringBuilder();
		boolean inQuotes = false;

		for (int i = 0; i < content.length(); i++) {
			char c = content.charAt(i);
			if (inQuotes) {
				if (c == '"') {
					if (i + 1 < content.length() && content.charAt(i + 1) == '"') {
						field.append('"');
						i++;
					} else {
						inQuotes = false;
					}
				} else {
					field.append(c);
				}
			} else if (c == '"') {
				inQuotes = true;
			} else if (c == ',') {
				fields.add(field.toString());
				field.setLength(0);
			} else if (c == '\n') {
				fields.add(field.toString());
				field.setLength(0);
				records.add(fields.toArray(new String[0]));
				fields.clear();
			} else if (c != '\r') {
				field.append(c);
			}
		}

		if (field.length() > 0 || !fields.isEmpty()) {
			fields.add(field.toString());
			records.add(fields.toArray(new String[0]));
		}
		return records;
	}

	// ------------------------- header handling -------------------------

	private Map<String, Integer> mapHeaders(String[] headers) {
		Map<String, Integer> headerIndex = new LinkedHashMap<>();
		for (int i = 0; i < headers.length; i++) {
			String field = fieldFor(headers[i]);
			if (field != null && !headerIndex.containsKey(field)) {
				headerIndex.put(field, i);
			}
		}
		if (headerIndex.isEmpty()) {
			throw new ServiceException("No valid header columns found. Expected headers like: " + String.join(", ",
					List.of("name", "fatherName", "motherName", "parentEmail", "parentPhone", "className", "dob",
							"address", "pincode", "gender")));
		}

		List<String> missing = REQUIRED_COLUMNS.stream().filter(c -> !headerIndex.containsKey(c))
				.collect(Collectors.toList());
		if (!missing.isEmpty()) {
			throw new ServiceException("Missing required column(s): " + String.join(", ", missing));
		}
		return headerIndex;
	}

	private String fieldFor(String header) {
		if (header == null) {
			return null;
		}
		String normalized = header.trim().toLowerCase().replaceAll("[^a-z0-9]", "");
		return HEADER_ALIASES.get(normalized);
	}

	// ------------------------- row mapping -------------------------

	private StudentImportRow toRow(Map<String, Object> values) {
		StudentImportRow row = new StudentImportRow();
		row.setName(asString(values.get("name")));
		row.setFatherName(asString(values.get("fatherName")));
		row.setMotherName(asString(values.get("motherName")));
		row.setParentEmail(asString(values.get("parentEmail")));
		row.setParentPhone(asString(values.get("parentPhone")));
		row.setClassName(asString(values.get("className")));
		row.setDob(toDate(values.get("dob")));
		row.setAddress(asString(values.get("address")));
		row.setPincode(asString(values.get("pincode")));
		row.setGender(toGender(values.get("gender")));
		return row;
	}

	private StudentRequest toRequest(StudentImportRow row, SchoolClass schoolClass) {
		StudentRequest request = new StudentRequest();
		request.setName(row.getName());
		request.setFatherName(row.getFatherName());
		request.setMotherName(row.getMotherName());
		request.setParentEmail(row.getParentEmail());
		request.setParentPhone(row.getParentPhone());
		request.setClassId(schoolClass.getId());
		request.setDob(row.getDob());
		request.setAddress(row.getAddress());
		request.setPincode(row.getPincode());
		request.setGender(row.getGender());
		return request;
	}

	private String asString(Object value) {
		if (value == null) {
			return null;
		}
		String text = value.toString().trim();
		return text.isEmpty() ? null : text;
	}

	private LocalDate toDate(Object value) {
		if (value == null || value.toString().isBlank()) {
			return null;
		}
		if (value instanceof LocalDate localDate) {
			return localDate;
		}
		if (value instanceof java.util.Date date) {
			return date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
		}

		String text = value.toString().trim();
		for (DateTimeFormatter formatter : DATE_FORMATTERS) {
			try {
				return LocalDate.parse(text, formatter);
			} catch (DateTimeParseException ignored) {
				// try the next format
			}
		}
		throw new ServiceException(
				"Invalid date of birth: " + text + " (expected yyyy-MM-dd or dd/MM/yyyy)");
	}

	private Gender toGender(Object value) {
		if (value == null || value.toString().isBlank()) {
			return null;
		}
		String text = value.toString().trim().toUpperCase();
		return switch (text) {
			case "MALE", "M" -> Gender.MALE;
			case "FEMALE", "F" -> Gender.FEMALE;
			case "OTHER", "O" -> Gender.OTHER;
			default -> throw new ServiceException(
					"Invalid gender: " + value + " (expected MALE, FEMALE or OTHER)");
		};
	}

	// ------------------------- cell helpers -------------------------

	private Object cellValue(Cell cell) {
		if (cell == null) {
			return null;
		}
		CellType type = cell.getCellType() == CellType.FORMULA ? cell.getCachedFormulaResultType()
				: cell.getCellType();
		return switch (type) {
			case STRING -> cell.getStringCellValue().trim();
			case NUMERIC -> DateUtil.isCellDateFormatted(cell)
					? cell.getLocalDateTimeCellValue().toLocalDate()
					: numericToString(cell.getNumericCellValue());
			case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
			default -> null;
		};
	}

	private String stringValue(Cell cell) {
		Object value = cellValue(cell);
		return value == null ? null : value.toString();
	}

	private String numericToString(double number) {
		if (number == Math.floor(number) && !Double.isInfinite(number)) {
			return String.valueOf((long) number);
		}
		return String.valueOf(number);
	}

	private String resolveExtension(String fileName) {
		if (fileName == null || !fileName.contains(".")) {
			throw new ServiceException("File name must end with .csv, .xls or .xlsx");
		}
		return fileName.substring(fileName.lastIndexOf('.') + 1).trim().toLowerCase();
	}

	private boolean isBlankRow(String[] cells) {
		if (cells == null) {
			return true;
		}
		for (String cell : cells) {
			if (cell != null && !cell.isBlank()) {
				return false;
			}
		}
		return true;
	}

	private void addError(List<String> errors, String message) {
		if (errors.size() < MAX_ERRORS) {
			errors.add(message);
		} else if (errors.size() == MAX_ERRORS) {
			errors.add("Too many errors, further error details are omitted.");
		}
	}

	private String rootMessage(Exception e) {
		Throwable cause = e;
		while (cause.getMessage() == null && cause.getCause() != null) {
			cause = cause.getCause();
		}
		return cause.getMessage() == null ? cause.getClass().getSimpleName() : cause.getMessage();
	}

	private record ParsedRow(int rowNumber, Map<String, Object> values) {
	}

}
