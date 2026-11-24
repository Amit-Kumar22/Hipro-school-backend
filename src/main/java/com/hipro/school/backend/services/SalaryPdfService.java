package com.hipro.school.backend.services;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.hipro.school.backend.entities.School;
import com.hipro.school.backend.entities.StaffSalary;
import com.hipro.school.backend.entities.StaffSalaryTemplate;
import com.hipro.school.backend.enums.SalaryStatus;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SalaryPdfService {

	private static final Font FONT_SCHOOL_NAME = new Font(FontFamily.HELVETICA, 16, Font.BOLD, new BaseColor(41, 128, 185));
	private static final Font FONT_SCHOOL_DETAIL = new Font(FontFamily.HELVETICA, 8, Font.NORMAL, BaseColor.GRAY);
	private static final Font FONT_TITLE = new Font(FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.DARK_GRAY);
	private static final Font FONT_SUBTITLE = new Font(FontFamily.HELVETICA, 9, Font.NORMAL, BaseColor.GRAY);
	private static final Font FONT_SECTION_HEADER = new Font(FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.WHITE);
	private static final Font FONT_LABEL = new Font(FontFamily.HELVETICA, 9, Font.NORMAL, BaseColor.DARK_GRAY);
	private static final Font FONT_VALUE = new Font(FontFamily.HELVETICA, 9, Font.BOLD, BaseColor.BLACK);
	private static final Font FONT_TOTAL_LABEL = new Font(FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.DARK_GRAY);
	private static final Font FONT_TOTAL_VALUE = new Font(FontFamily.HELVETICA, 13, Font.BOLD, new BaseColor(39, 174, 96));
	private static final Font FONT_NET_LABEL = new Font(FontFamily.HELVETICA, 11, Font.BOLD, new BaseColor(39, 174, 96));
	private static final Font FONT_FOOTER = new Font(FontFamily.HELVETICA, 7, Font.ITALIC, BaseColor.GRAY);
	private static final Font FONT_WATERMARK = new Font(FontFamily.HELVETICA, 40, Font.BOLD, new BaseColor(230, 230, 230));

	private static final BaseColor PRIMARY_BLUE = new BaseColor(41, 128, 185);
	private static final BaseColor HEADER_BG = new BaseColor(52, 73, 94);
	private static final BaseColor ROW_ALT_BG = new BaseColor(248, 249, 250);
	private static final BaseColor BORDER_COLOR = new BaseColor(220, 220, 220);
	private static final BaseColor NET_BG = new BaseColor(234, 250, 241);
	private static final BaseColor NET_BORDER = new BaseColor(39, 174, 96);
	private static final BaseColor STATUS_PAID_BG = new BaseColor(212, 237, 218);
	private static final BaseColor STATUS_PAID_FG = new BaseColor(21, 87, 36);
	private static final BaseColor STATUS_PENDING_BG = new BaseColor(255, 243, 205);
	private static final BaseColor STATUS_PENDING_FG = new BaseColor(133, 100, 4);

	private static final Font FONT_EARNINGS_HEADER = new Font(FontFamily.HELVETICA, 9, Font.BOLD, PRIMARY_BLUE);
	private static final Font FONT_DEDUCTIONS_HEADER = new Font(FontFamily.HELVETICA, 9, Font.BOLD, new BaseColor(192, 57, 43));
	private static final Font FONT_TOTAL_DEDUCTION = new Font(FontFamily.HELVETICA, 13, Font.BOLD, new BaseColor(192, 57, 43));
	private static final Font FONT_STATUS_VALUE = new Font(FontFamily.HELVETICA, 10, Font.BOLD, STATUS_PAID_FG);
	private static final Font FONT_ROW_STATUS = new Font(FontFamily.HELVETICA, 8, Font.ITALIC, BaseColor.GRAY);

	private static final com.itextpdf.text.pdf.BaseFont WATERMARK_FONT;
	private static volatile java.util.Map<String, Image> logoCache = new java.util.concurrent.ConcurrentHashMap<>();

	static {
		try {
			WATERMARK_FONT = com.itextpdf.text.pdf.BaseFont.createFont(
					com.itextpdf.text.pdf.BaseFont.HELVETICA,
					com.itextpdf.text.pdf.BaseFont.WINANSI,
					com.itextpdf.text.pdf.BaseFont.NOT_EMBEDDED);
		} catch (Exception e) {
			throw new RuntimeException("Failed to initialize watermark font", e);
		}
	}

	public byte[] generateSalaryPdf(StaffSalary salary, School school) {
		log.info("Generating salary PDF for salary id: {}", salary.getId());

		Document document = new Document(PageSize.A4, 30, 30, 30, 30);
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		try {
			PdfWriter writer = PdfWriter.getInstance(document, out);
			document.open();

			addWatermark(writer, salary);
			addSchoolHeader(document, school, salary);
			addTitleSection(document, salary);
			addEmployeeDetails(document, salary);
			addSalaryBreakdown(document, salary);
			addTotals(document, salary);
			addRemarks(document, salary);
			addFooter(document, school);

			document.close();
		} catch (Exception e) {
			log.error("Error generating salary PDF for salary id: {}", salary.getId(), e);
			throw new RuntimeException("Failed to generate salary PDF", e);
		}

		return out.toByteArray();
	}

	private void addWatermark(PdfWriter writer, StaffSalary salary) {
		com.itextpdf.text.pdf.PdfContentByte canvas = writer.getDirectContentUnder();
		canvas.beginText();
		canvas.setFontAndSize(WATERMARK_FONT, 40);
		canvas.setColorFill(new BaseColor(240, 240, 240));
		canvas.showTextAligned(Element.ALIGN_CENTER, "SALARY SLIP", PageSize.A4.getWidth() / 2,
				PageSize.A4.getHeight() / 2, 45);
		canvas.endText();
	}

	private void addSchoolHeader(Document document, School school, StaffSalary salary) throws DocumentException {
		PdfPTable headerTable = new PdfPTable(2);
		headerTable.setWidthPercentage(100);
		headerTable.setWidths(new float[]{75, 25});

		PdfPCell leftCell = new PdfPCell();
		leftCell.setBorder(Rectangle.NO_BORDER);
		leftCell.setPaddingBottom(10);

		if (school.getLogoUrl() != null && !school.getLogoUrl().isEmpty()) {
			try {
				Image logo = logoCache.computeIfAbsent(school.getLogoUrl(), url -> {
					try {
						Image img = Image.getInstance(url);
						img.scaleToFit(50, 50);
						return img;
					} catch (Exception e) {
						log.warn("Could not load school logo: {}", e.getMessage());
						return null;
					}
				});
				if (logo != null) {
					leftCell.addElement(logo);
				}
			} catch (Exception e) {
				log.warn("Could not load school logo: {}", e.getMessage());
			}
		}

		leftCell.addElement(new Paragraph(school.getSchoolName(), FONT_SCHOOL_NAME));

		StringBuilder details = new StringBuilder();
		if (school.getAddress() != null) {
			if (school.getAddress() != null) {
				details.append(school.getAddress().getBuildingName());
			}
			if (school.getAddress().getCity() != null) {
				if (details.length() > 0) details.append(", ");
				details.append(school.getAddress().getCity());
			}
			if (school.getAddress().getStateName() != null) {
				if (details.length() > 0) details.append(", ");
				details.append(school.getAddress().getStateName());
			}
		}
		if (details.length() > 0) {
			leftCell.addElement(new Paragraph(details.toString(), FONT_SCHOOL_DETAIL));
		}

		if (school.getPhoneNumber() != null) {
			leftCell.addElement(new Paragraph("Phone: " + school.getPhoneNumber(), FONT_SCHOOL_DETAIL));
		}
		if (school.getEmail() != null) {
			leftCell.addElement(new Paragraph("Email: " + school.getEmail(), FONT_SCHOOL_DETAIL));
		}

		PdfPCell rightCell = new PdfPCell();
		rightCell.setBorder(Rectangle.NO_BORDER);
		rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		rightCell.setVerticalAlignment(Element.ALIGN_TOP);

		PdfPTable rightInfo = new PdfPTable(1);
		rightInfo.setWidthPercentage(100);

		PdfPCell slipLabel = new PdfPCell(new Phrase("SALARY SLIP", FONT_TITLE));
		slipLabel.setBorder(Rectangle.NO_BORDER);
		slipLabel.setHorizontalAlignment(Element.ALIGN_RIGHT);
		slipLabel.setPaddingBottom(5);
		rightInfo.addCell(slipLabel);

		PdfPCell monthCell = new PdfPCell(
				new Phrase(salary.getSalaryMonth().format(DateTimeFormatter.ofPattern("MMMM yyyy")), FONT_SUBTITLE));
		monthCell.setBorder(Rectangle.NO_BORDER);
		monthCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		monthCell.setPaddingBottom(3);
		rightInfo.addCell(monthCell);

		PdfPCell idCell = new PdfPCell(new Phrase("Slip No: " + String.format("%06d", salary.getId()), FONT_SUBTITLE));
		idCell.setBorder(Rectangle.NO_BORDER);
		idCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		rightInfo.addCell(idCell);

		rightCell.addElement(rightInfo);
		leftCell.addElement(rightInfo);

		headerTable.addCell(leftCell);
		headerTable.addCell(new PdfPCell());
		document.add(headerTable);

		addHorizontalLine(document, PRIMARY_BLUE, 2);
	}

	private void addTitleSection(Document document, StaffSalary salary) throws DocumentException {
		PdfPTable table = new PdfPTable(3);
		table.setWidthPercentage(100);
		table.setWidths(new float[]{40, 30, 30});
		table.setSpacingBefore(8);
		table.setSpacingAfter(8);

		PdfPCell periodCell = createBorderedCell("Pay Period", FONT_LABEL,
				salary.getSalaryMonth().format(DateTimeFormatter.ofPattern("MMM yyyy")), FONT_VALUE);
		periodCell.setBackgroundColor(ROW_ALT_BG);

		PdfPCell genDateCell = createBorderedCell("Generated On", FONT_LABEL,
				java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy")), FONT_VALUE);
		genDateCell.setBackgroundColor(ROW_ALT_BG);

		PdfPCell statusCell = createStatusCell(salary.getStatus());

		table.addCell(periodCell);
		table.addCell(genDateCell);
		table.addCell(statusCell);

		document.add(table);
	}

	private void addEmployeeDetails(Document document, StaffSalary salary) throws DocumentException {
		PdfPTable sectionHeader = createSectionHeader("EMPLOYEE DETAILS");
		document.add(sectionHeader);

		PdfPTable table = new PdfPTable(4);
		table.setWidthPercentage(100);
		table.setWidths(new float[]{25, 25, 25, 25});
		table.setSpacingBefore(3);
		table.setSpacingAfter(8);

		addLabelValue(table, "Employee Name", salary.getUser().getFullName());
		addLabelValue(table, "Employee ID", "EMP-" + String.format("%04d", salary.getUser().getId()));
		addLabelValue(table, "Email", salary.getUser().getEmail());
		addLabelValue(table, "Payment Status", formatStatus(salary.getStatus()));

		document.add(table);
	}

	private void addSalaryBreakdown(Document document, StaffSalary salary) throws DocumentException {
		StaffSalaryTemplate template = salary.getGlobalTemplate();

		PdfPTable sectionHeader = createSectionHeader("SALARY BREAKDOWN");
		document.add(sectionHeader);

		PdfPTable table = new PdfPTable(3);
		table.setWidthPercentage(100);
		table.setWidths(new float[]{45, 25, 30});
		table.setSpacingBefore(3);

		// Column headers
		PdfPCell h1 = new PdfPCell(new Phrase("Description", FONT_SECTION_HEADER));
		h1.setBackgroundColor(HEADER_BG);
		h1.setPadding(7);
		h1.setBorder(Rectangle.NO_BORDER);

		PdfPCell h2 = new PdfPCell(new Phrase("Amount", FONT_SECTION_HEADER));
		h2.setBackgroundColor(HEADER_BG);
		h2.setPadding(7);
		h2.setHorizontalAlignment(Element.ALIGN_RIGHT);
		h2.setBorder(Rectangle.NO_BORDER);

		PdfPCell h3 = new PdfPCell(new Phrase("Status", FONT_SECTION_HEADER));
		h3.setBackgroundColor(HEADER_BG);
		h3.setPadding(7);
		h3.setHorizontalAlignment(Element.ALIGN_CENTER);
		h3.setBorder(Rectangle.NO_BORDER);

		table.addCell(h1);
		table.addCell(h2);
		table.addCell(h3);

		// Earnings sub-header
		PdfPCell earnSub = new PdfPCell(new Phrase("Earnings", FONT_EARNINGS_HEADER));
		earnSub.setColspan(3);
		earnSub.setPadding(5);
		earnSub.setBackgroundColor(new BaseColor(234, 250, 241));
		earnSub.setBorder(Rectangle.NO_BORDER);
		table.addCell(earnSub);

		addThreeColumnRow(table, "Basic Salary", formatCurrency(salary.getBasicSalary()), "Fixed", false);
		if (template.getHra() != null && template.getHra().compareTo(BigDecimal.ZERO) > 0) {
			addThreeColumnRow(table, "House Rent Allowance (HRA)", formatCurrency(template.getHra()), "Fixed", true);
		}
		if (template.getTransportAllowance() != null && template.getTransportAllowance().compareTo(BigDecimal.ZERO) > 0) {
			addThreeColumnRow(table, "Transport Allowance", formatCurrency(template.getTransportAllowance()), "Fixed", false);
		}
		if (template.getMedicalAllowance() != null && template.getMedicalAllowance().compareTo(BigDecimal.ZERO) > 0) {
			addThreeColumnRow(table, "Medical Allowance", formatCurrency(template.getMedicalAllowance()), "Fixed", true);
		}
		if (template.getOtherAllowance() != null && template.getOtherAllowance().compareTo(BigDecimal.ZERO) > 0) {
			addThreeColumnRow(table, "Other Allowance", formatCurrency(template.getOtherAllowance()), "Fixed", false);
		}

		// Total earnings row
		PdfPCell totalEarnLabel = new PdfPCell(new Phrase("Total Earnings", FONT_TOTAL_LABEL));
		totalEarnLabel.setPadding(7);
		totalEarnLabel.setBorder(Rectangle.TOP);
		totalEarnLabel.setBorderColor(NET_BORDER);
		totalEarnLabel.setBackgroundColor(NET_BG);

		PdfPCell totalEarnValue = new PdfPCell(new Phrase(formatCurrency(salary.getGrossSalary()), FONT_TOTAL_VALUE));
		totalEarnValue.setPadding(7);
		totalEarnValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
		totalEarnValue.setBorder(Rectangle.TOP);
		totalEarnValue.setBorderColor(NET_BORDER);
		totalEarnValue.setBackgroundColor(NET_BG);

		PdfPCell totalEarnStatus = new PdfPCell(new Phrase("", FONT_LABEL));
		totalEarnStatus.setBorder(Rectangle.TOP);
		totalEarnStatus.setBorderColor(NET_BORDER);
		totalEarnStatus.setBackgroundColor(NET_BG);

		table.addCell(totalEarnLabel);
		table.addCell(totalEarnValue);
		table.addCell(totalEarnStatus);

		// Deductions sub-header
		PdfPCell dedSub = new PdfPCell(new Phrase("Deductions", FONT_DEDUCTIONS_HEADER));
		dedSub.setColspan(3);
		dedSub.setPadding(5);
		dedSub.setBackgroundColor(new BaseColor(253, 237, 236));
		dedSub.setBorder(Rectangle.NO_BORDER);
		table.addCell(dedSub);

		if (template.getPf() != null && template.getPf().compareTo(BigDecimal.ZERO) > 0) {
			addThreeColumnRow(table, "Provident Fund (PF)", formatCurrency(template.getPf()), "Statutory", false);
		}
		if (template.getProfessionalTax() != null && template.getProfessionalTax().compareTo(BigDecimal.ZERO) > 0) {
			addThreeColumnRow(table, "Professional Tax", formatCurrency(template.getProfessionalTax()), "Statutory", true);
		}
		if (template.getOtherDeduction() != null && template.getOtherDeduction().compareTo(BigDecimal.ZERO) > 0) {
			addThreeColumnRow(table, "Other Deduction", formatCurrency(template.getOtherDeduction()), "Deduction", false);
		}

		// Total deductions row
		PdfPCell totalDedLabel = new PdfPCell(new Phrase("Total Deductions", FONT_TOTAL_LABEL));
		totalDedLabel.setPadding(7);
		totalDedLabel.setBorder(Rectangle.TOP);
		totalDedLabel.setBorderColor(new BaseColor(192, 57, 43));
		totalDedLabel.setBackgroundColor(new BaseColor(253, 237, 236));

		PdfPCell totalDedValue = new PdfPCell(
				new Phrase("(" + formatCurrency(salary.getTotalDeductions()) + ")", FONT_TOTAL_DEDUCTION));
		totalDedValue.setPadding(7);
		totalDedValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
		totalDedValue.setBorder(Rectangle.TOP);
		totalDedValue.setBorderColor(new BaseColor(192, 57, 43));
		totalDedValue.setBackgroundColor(new BaseColor(253, 237, 236));

		PdfPCell totalDedStatus = new PdfPCell(new Phrase("", FONT_LABEL));
		totalDedStatus.setBorder(Rectangle.TOP);
		totalDedStatus.setBorderColor(new BaseColor(192, 57, 43));
		totalDedStatus.setBackgroundColor(new BaseColor(253, 237, 236));

		table.addCell(totalDedLabel);
		table.addCell(totalDedValue);
		table.addCell(totalDedStatus);

		document.add(table);
	}

	private void addTotals(Document document, StaffSalary salary) throws DocumentException {
		PdfPTable table = new PdfPTable(2);
		table.setWidthPercentage(50);
		table.setHorizontalAlignment(Element.ALIGN_RIGHT);
		table.setSpacingBefore(10);

		PdfPCell netLabel = new PdfPCell(new Phrase("NET SALARY PAYABLE", FONT_NET_LABEL));
		netLabel.setBorder(Rectangle.BOX);
		netLabel.setBorderColor(NET_BORDER);
		netLabel.setBorderWidth(1.5f);
		netLabel.setPadding(10);
		netLabel.setBackgroundColor(NET_BG);

		PdfPCell netValue = new PdfPCell(new Phrase(formatCurrency(salary.getNetSalary()), FONT_TOTAL_VALUE));
		netValue.setBorder(Rectangle.BOX);
		netValue.setBorderColor(NET_BORDER);
		netValue.setBorderWidth(1.5f);
		netValue.setPadding(10);
		netValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
		netValue.setBackgroundColor(NET_BG);

		table.addCell(netLabel);
		table.addCell(netValue);

		document.add(table);

		// Attendance section
		if (salary.getWorkingDays() != null) {
			PdfPTable attHeader = createSectionHeader("ATTENDANCE SUMMARY");
			attHeader.setSpacingBefore(12);
			document.add(attHeader);

			PdfPTable attTable = new PdfPTable(3);
			attTable.setWidthPercentage(50);
			attTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
			attTable.setSpacingBefore(3);

			addLabelValueCompact(attTable, "Working Days", salary.getWorkingDays() != null ? String.valueOf(salary.getWorkingDays()) : "-");
			addLabelValueCompact(attTable, "Paid Days", salary.getPaidDays() != null ? String.valueOf(salary.getPaidDays()) : "-");
			addLabelValueCompact(attTable, "Unpaid Days", salary.getUnpaidDays() != null ? String.valueOf(salary.getUnpaidDays()) : "-");

			document.add(attTable);
		}

		// Payment info
		if (salary.getPaymentDate() != null || salary.getPaymentReference() != null) {
			PdfPTable payTable = new PdfPTable(2);
			payTable.setWidthPercentage(50);
			payTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
			payTable.setSpacingBefore(8);

			if (salary.getPaymentDate() != null) {
				addLabelValueCompact(payTable, "Payment Date",
						salary.getPaymentDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
			}
			if (salary.getPaymentReference() != null) {
				addLabelValueCompact(payTable, "Reference No", salary.getPaymentReference());
			}

			document.add(payTable);
		}
	}

	private void addRemarks(Document document, StaffSalary salary) throws DocumentException {
		if (salary.getRemarks() != null && !salary.getRemarks().isEmpty()) {
			PdfPTable table = new PdfPTable(1);
			table.setWidthPercentage(100);
			table.setSpacingBefore(10);

			PdfPTable header = createSectionHeader("REMARKS");
			table.addCell(header);

			PdfPCell remarkCell = new PdfPCell(new Phrase(salary.getRemarks(), FONT_LABEL));
			remarkCell.setPadding(8);
			remarkCell.setBorder(Rectangle.BOX);
			remarkCell.setBorderColor(BORDER_COLOR);
			remarkCell.setBackgroundColor(ROW_ALT_BG);
			table.addCell(remarkCell);

			document.add(table);
		}
	}

	private void addFooter(Document document, School school) throws DocumentException {
		document.add(new Paragraph("\n"));
		addHorizontalLine(document, BORDER_COLOR, 1);

		PdfPTable footerTable = new PdfPTable(2);
		footerTable.setWidthPercentage(100);
		footerTable.setSpacingBefore(5);

		PdfPCell leftFooter = new PdfPCell();
		leftFooter.setBorder(Rectangle.NO_BORDER);
		leftFooter.addElement(new Paragraph("Generated by " + school.getSchoolName(), FONT_FOOTER));
		leftFooter.addElement(new Paragraph("This is a computer-generated document. No physical signature required.", FONT_FOOTER));

		PdfPCell rightFooter = new PdfPCell();
		rightFooter.setBorder(Rectangle.NO_BORDER);
		rightFooter.setHorizontalAlignment(Element.ALIGN_RIGHT);
		rightFooter.addElement(new Paragraph("For queries, contact " + (school.getEmail() != null ? school.getEmail() : "admin"),
				FONT_FOOTER));

		footerTable.addCell(leftFooter);
		footerTable.addCell(rightFooter);
		document.add(footerTable);
	}

	// Helper methods

	private PdfPTable createSectionHeader(String title) {
		PdfPTable headerTable = new PdfPTable(1);
		headerTable.setWidthPercentage(100);

		PdfPCell cell = new PdfPCell(new Phrase(title, FONT_SECTION_HEADER));
		cell.setBackgroundColor(HEADER_BG);
		cell.setPadding(7);
		cell.setBorder(Rectangle.NO_BORDER);
		headerTable.addCell(cell);

		return headerTable;
	}

	private PdfPCell createBorderedCell(String label, Font labelFont, String value, Font valueFont) {
		PdfPTable inner = new PdfPTable(1);
		inner.setWidthPercentage(100);

		PdfPCell l = new PdfPCell(new Phrase(label, labelFont));
		l.setBorder(Rectangle.NO_BORDER);
		l.setPadding(2);

		PdfPCell v = new PdfPCell(new Phrase(value, valueFont));
		v.setBorder(Rectangle.NO_BORDER);
		v.setPadding(2);

		inner.addCell(l);
		inner.addCell(v);

		PdfPCell cell = new PdfPCell(inner);
		cell.setBorder(Rectangle.BOX);
		cell.setBorderColor(BORDER_COLOR);
		cell.setPadding(5);
		return cell;
	}

	private PdfPCell createStatusCell(SalaryStatus status) {
		PdfPCell cell = new PdfPCell();
		cell.setBorder(Rectangle.BOX);
		cell.setBorderColor(BORDER_COLOR);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);

		PdfPTable inner = new PdfPTable(1);
		inner.setWidthPercentage(100);

		PdfPCell label = new PdfPCell(new Phrase("Status", FONT_LABEL));
		label.setBorder(Rectangle.NO_BORDER);
		label.setHorizontalAlignment(Element.ALIGN_CENTER);
		label.setPadding(2);

		PdfPCell value = new PdfPCell(new Phrase(formatStatus(status),
				new Font(FontFamily.HELVETICA, 10, Font.BOLD, getStatusColor(status))));
		value.setBorder(Rectangle.NO_BORDER);
		value.setHorizontalAlignment(Element.ALIGN_CENTER);
		value.setBackgroundColor(getStatusBg(status));
		value.setPadding(4);

		inner.addCell(label);
		inner.addCell(value);
		cell.addElement(inner);
		return cell;
	}

	private void addLabelValue(PdfPTable table, String label, String value) {
		PdfPCell labelCell = new PdfPCell(new Phrase(label, FONT_LABEL));
		labelCell.setBorder(Rectangle.BOTTOM);
		labelCell.setBorderColor(BORDER_COLOR);
		labelCell.setPadding(5);
		labelCell.setBackgroundColor(ROW_ALT_BG);

		PdfPCell valueCell = new PdfPCell(new Phrase(value, FONT_VALUE));
		valueCell.setBorder(Rectangle.BOTTOM);
		valueCell.setBorderColor(BORDER_COLOR);
		valueCell.setPadding(5);

		table.addCell(labelCell);
		table.addCell(valueCell);
	}

	private void addLabelValueCompact(PdfPTable table, String label, String value) {
		PdfPCell labelCell = new PdfPCell(new Phrase(label, FONT_LABEL));
		labelCell.setBorder(Rectangle.BOTTOM);
		labelCell.setBorderColor(BORDER_COLOR);
		labelCell.setPadding(4);

		PdfPCell valueCell = new PdfPCell(new Phrase(value, FONT_VALUE));
		valueCell.setBorder(Rectangle.BOTTOM);
		valueCell.setBorderColor(BORDER_COLOR);
		valueCell.setPadding(4);
		valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

		table.addCell(labelCell);
		table.addCell(valueCell);
	}

	private void addThreeColumnRow(PdfPTable table, String label, String amount, String status, boolean alternate) {
		PdfPCell labelCell = new PdfPCell(new Phrase(label, FONT_LABEL));
		labelCell.setPadding(6);
		labelCell.setBorder(Rectangle.BOTTOM);
		labelCell.setBorderColor(BORDER_COLOR);
		if (alternate) labelCell.setBackgroundColor(ROW_ALT_BG);

		PdfPCell amountCell = new PdfPCell(new Phrase(amount, FONT_VALUE));
		amountCell.setPadding(6);
		amountCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		amountCell.setBorder(Rectangle.BOTTOM);
		amountCell.setBorderColor(BORDER_COLOR);
		if (alternate) amountCell.setBackgroundColor(ROW_ALT_BG);

		PdfPCell statusCell = new PdfPCell(new Phrase(status, FONT_ROW_STATUS));
		statusCell.setPadding(6);
		statusCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		statusCell.setBorder(Rectangle.BOTTOM);
		statusCell.setBorderColor(BORDER_COLOR);
		if (alternate) statusCell.setBackgroundColor(ROW_ALT_BG);

		table.addCell(labelCell);
		table.addCell(amountCell);
		table.addCell(statusCell);
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

	private String formatCurrency(BigDecimal amount) {
		if (amount == null) {
			return "\u20B9 0.00";
		}
		return "\u20B9 " + String.format("%,.2f", amount);
	}

	private String formatStatus(SalaryStatus status) {
		if (status == null) {
			return "Draft";
		}
		switch (status) {
			case PAID: return "Paid";
			case APPROVED: return "Approved";
			case GENERATED: return "Generated";
			case CANCELLED: return "Cancelled";
			default: return "Draft";
		}
	}

	private BaseColor getStatusColor(SalaryStatus status) {
		if (status == null) return BaseColor.GRAY;
		switch (status) {
			case PAID: return STATUS_PAID_FG;
			case CANCELLED: return new BaseColor(192, 57, 43);
			case APPROVED: return PRIMARY_BLUE;
			default: return STATUS_PENDING_FG;
		}
	}

	private BaseColor getStatusBg(SalaryStatus status) {
		if (status == null) return ROW_ALT_BG;
		switch (status) {
			case PAID: return STATUS_PAID_BG;
			case CANCELLED: return new BaseColor(253, 237, 236);
			case APPROVED: return new BaseColor(214, 234, 248);
			default: return STATUS_PENDING_BG;
		}
	}
}
