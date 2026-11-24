package com.hipro.school.backend.services;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.hipro.school.backend.entities.ExamResult;
import com.hipro.school.backend.entities.School;
import com.hipro.school.backend.entities.Student;
import com.hipro.school.backend.exceptions.ServiceException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ExamResultPdfService {

	private static final Font FONT_SCHOOL_NAME = new Font(FontFamily.HELVETICA, 16, Font.BOLD,
			new BaseColor(41, 128, 185));
	private static final Font FONT_SCHOOL_DETAIL = new Font(FontFamily.HELVETICA, 8, Font.NORMAL, BaseColor.GRAY);
	private static final Font FONT_TITLE = new Font(FontFamily.HELVETICA, 13, Font.BOLD, BaseColor.DARK_GRAY);
	private static final Font FONT_SUBTITLE = new Font(FontFamily.HELVETICA, 8, Font.NORMAL, BaseColor.GRAY);
	private static final Font FONT_SECTION_HEADER = new Font(FontFamily.HELVETICA, 8, Font.BOLD, BaseColor.WHITE);
	private static final Font FONT_STUDENT = new Font(FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.WHITE);
	private static final Font FONT_CELL = new Font(FontFamily.HELVETICA, 8, Font.NORMAL, BaseColor.BLACK);
	private static final Font FONT_TOTAL = new Font(FontFamily.HELVETICA, 8, Font.BOLD, BaseColor.DARK_GRAY);
	private static final Font FONT_FOOTER = new Font(FontFamily.HELVETICA, 7, Font.ITALIC, BaseColor.GRAY);

	private static final BaseColor PRIMARY_BLUE = new BaseColor(41, 128, 185);
	private static final BaseColor HEADER_BG = new BaseColor(52, 73, 94);
	private static final BaseColor STUDENT_BG = new BaseColor(41, 128, 185);
	private static final BaseColor ROW_ALT_BG = new BaseColor(248, 249, 250);
	private static final BaseColor TOTAL_BG = new BaseColor(234, 250, 241);
	private static final BaseColor BORDER_COLOR = new BaseColor(220, 220, 220);
	private static final BaseColor TOTAL_BORDER = new BaseColor(39, 174, 96);

	private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy");

	public byte[] generateResultsPdf(List<ExamResult> results, School school) {
		log.info("Generating exam results PDF for {} result(s)", results.size());

		Document document = new Document(PageSize.A4, 30, 30, 30, 30);
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		try {
			PdfWriter.getInstance(document, out);
			document.open();

			Map<Long, List<ExamResult>> byStudent = groupByStudent(results);

			addSchoolHeader(document, school);
			addTitle(document, results.size(), byStudent.size());
			for (List<ExamResult> studentResults : byStudent.values()) {
				addStudentSection(document, studentResults);
			}
			addFooter(document, school);

			document.close();
		} catch (Exception e) {
			log.error("Error generating exam results PDF", e);
			throw new ServiceException("Failed to generate exam results PDF: " + e.getMessage());
		}

		return out.toByteArray();
	}

	private Map<Long, List<ExamResult>> groupByStudent(List<ExamResult> results) {
		Map<Long, List<ExamResult>> grouped = new LinkedHashMap<>();
		for (ExamResult result : results) {
			Long studentId = result.getStudent() != null ? result.getStudent().getId() : null;
			grouped.computeIfAbsent(studentId, key -> new ArrayList<>()).add(result);
		}
		return grouped;
	}

	private void addSchoolHeader(Document document, School school) throws DocumentException {
		Paragraph schoolName = new Paragraph(
				school.getSchoolName() != null ? school.getSchoolName() : "School", FONT_SCHOOL_NAME);
		schoolName.setAlignment(Element.ALIGN_CENTER);
		document.add(schoolName);

		StringBuilder details = new StringBuilder();
		if (school.getAddress() != null) {
			appendDetail(details, school.getAddress().getBuildingName());
			appendDetail(details, school.getAddress().getCity());
			appendDetail(details, school.getAddress().getStateName());
		}
		if (school.getPhoneNumber() != null) {
			appendDetail(details, "Phone: " + school.getPhoneNumber());
		}
		if (school.getEmail() != null) {
			appendDetail(details, "Email: " + school.getEmail());
		}
		if (details.length() > 0) {
			Paragraph detailLine = new Paragraph(details.toString(), FONT_SCHOOL_DETAIL);
			detailLine.setAlignment(Element.ALIGN_CENTER);
			document.add(detailLine);
		}

		addHorizontalLine(document, PRIMARY_BLUE, 2);
	}

	private void addTitle(Document document, int resultCount, int studentCount) throws DocumentException {
		Paragraph title = new Paragraph("STUDENT EXAM RESULTS", FONT_TITLE);
		title.setAlignment(Element.ALIGN_CENTER);
		title.setSpacingBefore(10);
		document.add(title);

		String subtitle = resultCount + " result(s) across " + studentCount + " student(s)  |  Generated on "
				+ LocalDate.now().format(DATE_FORMAT);
		Paragraph subtitleLine = new Paragraph(subtitle, FONT_SUBTITLE);
		subtitleLine.setAlignment(Element.ALIGN_CENTER);
		subtitleLine.setSpacingAfter(4);
		document.add(subtitleLine);
	}

	private void addStudentSection(Document document, List<ExamResult> results) throws DocumentException {
		Student student = results.get(0).getStudent();
		document.add(createStudentHeader(student));
		document.add(createResultTable(results));
	}

	private PdfPTable createStudentHeader(Student student) {
		PdfPTable table = new PdfPTable(1);
		table.setWidthPercentage(100);
		table.setSpacingBefore(10);

		StringBuilder header = new StringBuilder();
		header.append(student != null && student.getStudentUser() != null && student.getStudentUser().getFullName() != null
				? student.getStudentUser().getFullName()
				: "Student");
		if (student != null && student.getAdmissionNumber() != null) {
			header.append("   |   Admission No: ").append(student.getAdmissionNumber());
		}
		if (student != null && student.getSchoolClass() != null && student.getSchoolClass().getClassName() != null) {
			header.append("   |   Class: ").append(student.getSchoolClass().getClassName());
		}
		if (student != null && student.getRollNumber() != null && !student.getRollNumber().isBlank()) {
			header.append("   |   Roll No: ").append(student.getRollNumber());
		}

		PdfPCell cell = new PdfPCell(new Phrase(header.toString(), FONT_STUDENT));
		cell.setBackgroundColor(STUDENT_BG);
		cell.setPadding(7);
		cell.setBorder(Rectangle.NO_BORDER);
		table.addCell(cell);
		return table;
	}

	private PdfPTable createResultTable(List<ExamResult> results) throws DocumentException {
		PdfPTable table = new PdfPTable(8);
		table.setWidthPercentage(100);
		table.setWidths(new float[] { 20, 17, 12, 10, 11, 10, 9, 11 });
		table.setSpacingBefore(4);
		table.setSpacingAfter(6);

		String[] headers = { "Exam", "Subject", "Date", "Total", "Obtained", "%", "Grade", "Result" };
		for (String header : headers) {
			PdfPCell cell = new PdfPCell(new Phrase(header, FONT_SECTION_HEADER));
			cell.setBackgroundColor(HEADER_BG);
			cell.setPadding(5);
			cell.setBorder(Rectangle.NO_BORDER);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			table.addCell(cell);
		}

		int rowIndex = 0;
		for (ExamResult result : results) {
			boolean alternate = rowIndex++ % 2 == 1;
			addCell(table, result.getExam() != null ? result.getExam().getTitle() : "-", Element.ALIGN_LEFT, alternate,
					1, FONT_CELL, alternate ? ROW_ALT_BG : null, false);
			addCell(table, subjectName(result), Element.ALIGN_LEFT, alternate, 1, FONT_CELL,
					alternate ? ROW_ALT_BG : null, false);
			addCell(table, examDate(result), Element.ALIGN_CENTER, alternate, 1, FONT_CELL,
					alternate ? ROW_ALT_BG : null, false);
			addCell(table, formatAmount(result.getTotalMarks()), Element.ALIGN_RIGHT, alternate, 1, FONT_CELL,
					alternate ? ROW_ALT_BG : null, false);
			addCell(table, obtainedText(result), Element.ALIGN_RIGHT, alternate, 1, FONT_CELL,
					alternate ? ROW_ALT_BG : null, false);
			addCell(table, formatPercentage(result.getPercentage()), Element.ALIGN_RIGHT, alternate, 1, FONT_CELL,
					alternate ? ROW_ALT_BG : null, false);
			addCell(table, result.getGrade() != null ? result.getGrade() : "-", Element.ALIGN_CENTER, alternate, 1,
					FONT_CELL, alternate ? ROW_ALT_BG : null, false);
			addCell(table, resultText(result), Element.ALIGN_CENTER, alternate, 1, FONT_CELL,
					alternate ? ROW_ALT_BG : null, false);
		}

		addOverallRow(table, results);
		return table;
	}

	private void addOverallRow(PdfPTable table, List<ExamResult> results) {
		BigDecimal totalMarks = BigDecimal.ZERO;
		BigDecimal obtainedMarks = BigDecimal.ZERO;
		for (ExamResult result : results) {
			if (result.getTotalMarks() != null) {
				totalMarks = totalMarks.add(result.getTotalMarks());
			}
			if (!Boolean.TRUE.equals(result.getAbsent()) && result.getObtainedMarks() != null) {
				obtainedMarks = obtainedMarks.add(result.getObtainedMarks());
			}
		}

		BigDecimal percentage = totalMarks.compareTo(BigDecimal.ZERO) > 0
				? obtainedMarks.multiply(BigDecimal.valueOf(100)).divide(totalMarks, 2, RoundingMode.HALF_UP)
				: null;

		addCell(table, "Overall", Element.ALIGN_LEFT, false, 3, FONT_TOTAL, TOTAL_BG, true);
		addCell(table, formatAmount(totalMarks), Element.ALIGN_RIGHT, false, 1, FONT_TOTAL, TOTAL_BG, true);
		addCell(table, formatAmount(obtainedMarks), Element.ALIGN_RIGHT, false, 1, FONT_TOTAL, TOTAL_BG, true);
		addCell(table, formatPercentage(percentage), Element.ALIGN_RIGHT, false, 1, FONT_TOTAL, TOTAL_BG, true);
		addCell(table, percentage != null ? ExamResultService.calculateGrade(percentage) : "-", Element.ALIGN_CENTER,
				false, 1, FONT_TOTAL, TOTAL_BG, true);
		addCell(table, overallResult(results), Element.ALIGN_CENTER, false, 1, FONT_TOTAL, TOTAL_BG, true);
	}

	private void addCell(PdfPTable table, String text, int alignment, boolean alternate, int colspan, Font font,
			BaseColor background, boolean topBorder) {
		PdfPCell cell = new PdfPCell(new Phrase(text != null ? text : "-", font));
		cell.setColspan(colspan);
		cell.setHorizontalAlignment(alignment);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setPadding(5);
		cell.setBorder(Rectangle.NO_BORDER);
		if (topBorder) {
			cell.setBorder(Rectangle.TOP);
			cell.setBorderColor(TOTAL_BORDER);
			cell.setBorderWidthTop(1.2f);
		}
		if (background != null) {
			cell.setBackgroundColor(background);
		}
		table.addCell(cell);
	}

	private String subjectName(ExamResult result) {
		if (result.getExamSubject() == null || result.getExamSubject().getSubject() == null) {
			return "-";
		}
		return result.getExamSubject().getSubject().getSubjectName();
	}

	private String examDate(ExamResult result) {
		if (result.getExamSubject() == null || result.getExamSubject().getExamDate() == null) {
			return "-";
		}
		return result.getExamSubject().getExamDate().format(DATE_FORMAT);
	}

	private String obtainedText(ExamResult result) {
		if (Boolean.TRUE.equals(result.getAbsent())) {
			return "Absent";
		}
		return formatAmount(result.getObtainedMarks());
	}

	private String resultText(ExamResult result) {
		if (Boolean.TRUE.equals(result.getAbsent())) {
			return "Absent";
		}
		if (result.getPassed() == null) {
			return "-";
		}
		return result.getPassed() ? "Pass" : "Fail";
	}

	private String overallResult(List<ExamResult> results) {
		boolean anyPresent = false;
		for (ExamResult result : results) {
			if (Boolean.TRUE.equals(result.getAbsent())) {
				continue;
			}
			anyPresent = true;
			if (!Boolean.TRUE.equals(result.getPassed())) {
				return "Fail";
			}
		}
		return anyPresent ? "Pass" : "-";
	}

	private String formatAmount(BigDecimal value) {
		if (value == null) {
			return "-";
		}
		return value.setScale(2, RoundingMode.HALF_UP).toPlainString();
	}

	private String formatPercentage(BigDecimal value) {
		if (value == null) {
			return "-";
		}
		return value.setScale(2, RoundingMode.HALF_UP).toPlainString() + "%";
	}

	private void addFooter(Document document, School school) throws DocumentException {
		addHorizontalLine(document, BORDER_COLOR, 1);

		PdfPTable footerTable = new PdfPTable(1);
		footerTable.setWidthPercentage(100);
		footerTable.setSpacingBefore(4);

		PdfPCell cell = new PdfPCell();
		cell.setBorder(Rectangle.NO_BORDER);
		cell.addElement(new Paragraph(
				"Generated by " + (school.getSchoolName() != null ? school.getSchoolName() : "School"), FONT_FOOTER));
		cell.addElement(new Paragraph("This is a computer-generated document. No physical signature required.",
				FONT_FOOTER));
		footerTable.addCell(cell);
		document.add(footerTable);
	}

	private void addHorizontalLine(Document document, BaseColor color, float width) throws DocumentException {
		PdfPTable lineTable = new PdfPTable(1);
		lineTable.setWidthPercentage(100);
		PdfPCell cell = new PdfPCell();
		cell.setBorder(Rectangle.BOTTOM);
		cell.setBorderColor(color);
		cell.setBorderWidth(width);
		cell.setFixedHeight(1);
		lineTable.addCell(cell);
		document.add(lineTable);
	}

	private void appendDetail(StringBuilder builder, String value) {
		if (value == null || value.isBlank()) {
			return;
		}
		if (builder.length() > 0) {
			builder.append(", ");
		}
		builder.append(value);
	}
}
