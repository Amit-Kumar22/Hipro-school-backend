package com.hipro.school.backend.services;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hipro.school.backend.dtos.LeadDto;
import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.entities.SchoolLead;
import com.hipro.school.backend.enums.LeadStatus;
import com.hipro.school.backend.repositories.SchoolLeadRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class JustdialSchoolService {

	private final SchoolLeadRepository schoolLeadRepository;

	private static final String JUSTDIAL_BASE_URL = "https://www.justdial.com";

	private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " + "AppleWebKit/537.36 "
			+ "(KHTML, like Gecko) " + "Chrome/151.0.0.0 Safari/537.36";

	private static final Pattern PINCODE_PATTERN = Pattern.compile("\\b\\d{6}\\b");

	private static final Pattern PHONE_PATTERN = Pattern.compile("(?:\\+91[-\\s]?)?[6-9]\\d{9}");

	private static final Pattern EMAIL_PATTERN = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}");

	private static final Pattern RATING_PATTERN = Pattern.compile("\\b([0-5](?:\\.\\d)?)\\b");

	private static final Pattern REVIEW_PATTERN = Pattern.compile("(\\d[\\d,]*)\\s*(?:ratings?|reviews?)",
			Pattern.CASE_INSENSITIVE);

	public PageResponse<SchoolLead> getAllLeads(Pageable pageable) {
		log.info("Fetching all school leads with pageable: {}", pageable);
		Page<SchoolLead> page = schoolLeadRepository.findAll(pageable);
		return PageResponse.from(page);
	}

	/**
	 * Main import method.
	 */
	@Transactional
	public List<SchoolLead> fetchAndSaveSchools(String city, String category) {

		log.info("Starting Justdial import. city={}, category={}", city, category);

		List<SchoolLead> savedLeads = new ArrayList<>();

		try {

			String url = buildSearchUrl(city, category);

			log.info("Fetching Justdial category URL: {}", url);

			Document document = fetchPage(url);

			if (document == null) {
				throw new RuntimeException("Unable to fetch Justdial page");
			}

			log.info("Justdial page received. title={}, finalUrl={}, htmlLength={}", document.title(),
					document.location(), document.html().length());

			Elements listings = findListings(document, city);

			log.info("Found {} Justdial listings for city={}", listings.size(), city);

			if (listings.isEmpty()) {

				log.warn("No listings found. title={}, htmlLength={}, links={}", document.title(),
						document.html().length(), document.select("a[href]").size());

				return savedLeads;
			}

			int count = 0;

			for (Element listing : listings) {

				try {

					count++;

					log.info("Processing listing {}/{}", count, listings.size());

					LeadDto dto = parseListing(listing, city);

					if (dto == null) {
						log.warn("Unable to parse listing");
						continue;
					}

					if (dto.getSchoolName() == null || dto.getSchoolName().isBlank()) {

						log.warn("Skipping listing because school name is empty");

						continue;
					}

					/*
					 * Open the individual Justdial detail page to get additional information.
					 */
					if (dto.getJustdialUrl() != null && !dto.getJustdialUrl().isBlank()) {

						log.info("Fetching detail page for: {}", dto.getSchoolName());

						Document detailPage = fetchPage(dto.getJustdialUrl());

						if (detailPage != null) {

							enrichLeadFromDetailPage(dto, detailPage);
						}

						/*
						 * Small delay between detail requests.
						 */
						sleep(500);
					}

					SchoolLead lead = saveOrUpdateLead(dto);

					if (lead != null) {

						savedLeads.add(lead);

						log.info("Saved school: id={}, name={}", lead.getId(), lead.getSchoolName());
					}

				} catch (Exception e) {

					log.error("Failed to process Justdial listing", e);
				}
			}

			log.info("Justdial import completed. Found={}, Saved={}", listings.size(), savedLeads.size());

			return savedLeads;

		} catch (Exception e) {

			log.error("Failed to fetch schools from Justdial", e);

			throw new RuntimeException("Unable to fetch school data from Justdial", e);
		}
	}

	/**
	 * Build Justdial category URL.
	 *
	 * Example: https://www.justdial.com/Gorakhpur/Schools/nct-10422444
	 */
	private String buildSearchUrl(String city, String category) {

		String formattedCity = city.trim().replace(" ", "-");

		String formattedCategory = category.trim().replace(" ", "-");

		/*
		 * Schools category id.
		 */
		return JUSTDIAL_BASE_URL + "/" + formattedCity + "/" + formattedCategory + "/nct-10422444"
				+ "?trkid=1255-remotecity-fcat-catsle" + "&term=" + formattedCategory + "&cbflg=";
	}

	/**
	 * Fetch a page from Justdial.
	 */
	private Document fetchPage(String url) {

		try {

			return Jsoup.connect(url).userAgent(USER_AGENT)
					.header("Accept",
							"text/html,application/xhtml+xml," + "application/xml;q=0.9," + "image/avif,image/webp,"
									+ "*/*;q=0.8")
					.header("Accept-Language", "en-US,en;q=0.9").header("Cache-Control", "no-cache")
					.header("Pragma", "no-cache").referrer("https://www.google.com/").timeout(60000)
					.followRedirects(true).get();

		} catch (Exception e) {

			log.error("Failed to fetch URL: {}", url, e);

			return null;
		}
	}

	/**
	 * Find unique listing containers from category page.
	 */
	private Elements findListings(Document document, String city) {

		Map<String, Element> uniqueListings = new LinkedHashMap<>();

		Elements links = document.select("a[href]");

		log.info("Total links found on page: {}", links.size());

		for (Element link : links) {

			String href = link.attr("abs:href");

			String text = clean(link.text());

			if (href == null || href.isBlank()) {
				continue;
			}

			if (text == null || text.isBlank()) {
				continue;
			}

			/*
			 * Only links for this city.
			 */
			if (!href.toLowerCase().contains("/" + city.toLowerCase() + "/")) {

				continue;
			}

			String lowerHref = href.toLowerCase();

			/*
			 * Ignore generic pages.
			 */
			if (lowerHref.contains("/schools") || lowerHref.contains("/school/") || lowerHref.contains("/search")
					|| lowerHref.contains("/category") || lowerHref.contains("/login") || lowerHref.contains("/contact")
					|| lowerHref.contains("/about") || lowerHref.contains("/blog")) {

				continue;
			}

			/*
			 * Only Justdial links.
			 */
			if (!lowerHref.contains("justdial.com")) {

				continue;
			}

			/*
			 * Move up the DOM and identify listing.
			 */
			Element container = findListingContainer(link);

			if (container == null) {
				continue;
			}

			/*
			 * URL is unique enough for listing page.
			 */
			uniqueListings.putIfAbsent(href, container);
		}

		log.info("Unique listing containers found: {}", uniqueListings.size());

		Elements result = new Elements();

		result.addAll(uniqueListings.values());

		return result;
	}

	/**
	 * Find parent element that contains the complete listing information.
	 */
	private Element findListingContainer(Element link) {

		Element current = link;

		/*
		 * First try known Justdial containers.
		 */
		Element known = link.closest(".cntanr");

		if (known != null) {
			return known;
		}

		known = link.closest(".store-details");

		if (known != null) {
			return known;
		}

		/*
		 * Generic fallback.
		 */
		for (int i = 0; i < 10 && current != null; i++) {

			String text = clean(current.text());

			if (text == null) {
				current = current.parent();
				continue;
			}

			boolean hasPhone = PHONE_PATTERN.matcher(text).find();

			boolean hasAddress = text.length() > 80;

			boolean hasHeading = current.select("h1,h2,h3").size() > 0;

			if (hasHeading && (hasPhone || hasAddress)) {

				return current;
			}

			current = current.parent();
		}

		return link.parent();
	}

	/**
	 * Parse listing from category page.
	 */
	private LeadDto parseListing(Element listing, String city) {

		String schoolName = extractSchoolName(listing);

		if (schoolName == null || schoolName.isBlank()) {

			return null;
		}

		String address = extractAddress(listing, city);

		String phone = extractPhone(listing);

		if (phone == null) {

			phone = extractPhoneFromText(listing.text());
		}

		String justdialUrl = extractListingUrl(listing);

		String justdialId = extractJustdialId(listing, justdialUrl);

		String text = clean(listing.text());

		return LeadDto.builder().schoolName(clean(schoolName)).address(clean(address)).locality(null).city(city)
				.state(extractState(address)).pincode(extractPincode(address)).phone(clean(phone)).alternatePhone(null)
				.email(null).website(null).category("SCHOOL").board(extractBoard(text)).schoolType(null)
				.justdialUrl(justdialUrl).justdialId(justdialId).rating(extractRating(text))
				.reviewCount(extractReviewCount(text)).build();
	}

	/**
	 * Enrich DTO using individual school detail page.
	 */
	private void enrichLeadFromDetailPage(LeadDto dto, Document document) {

		if (document == null) {
			return;
		}

		String pageText = clean(document.text());

		/*
		 * Phone
		 */
		String phone = extractPhoneFromDetailPage(document);

		if (phone != null) {
			dto.setPhone(phone);
		}

		/*
		 * Email
		 */
		String email = extractEmail(document);

		if (email != null) {
			dto.setEmail(email);
		}

		/*
		 * Website
		 */
		String website = extractExternalWebsite(document);

		if (website != null) {
			dto.setWebsite(website);
		}

		/*
		 * Address
		 */
		String address = extractDetailAddress(document);

		if (address != null) {

			dto.setAddress(address);

			dto.setPincode(extractPincode(address));

			dto.setState(extractState(address));
		}

		/*
		 * Rating
		 */
		Double rating = extractRating(pageText);

		if (rating != null) {
			dto.setRating(rating);
		}

		/*
		 * Reviews
		 */
		Integer reviewCount = extractReviewCount(pageText);

		if (reviewCount != null) {
			dto.setReviewCount(reviewCount);
		}

		/*
		 * Board
		 */
		String board = extractBoard(pageText);

		if (board != null) {
			dto.setBoard(board);
		}

		/*
		 * School type.
		 */
		dto.setSchoolType(extractSchoolType(pageText));

		/*
		 * Locality.
		 */
		dto.setLocality(extractLocality(dto.getAddress(), dto.getCity()));
	}

	/**
	 * Extract school name.
	 */
	private String extractSchoolName(Element listing) {

		String[] selectors = {

				".lng_cont_name",

				".store-name",

				"h1",

				"h2",

				"h3",

				"[class*=name]",

				"[class*=title]" };

		for (String selector : selectors) {

			Element element = listing.selectFirst(selector);

			if (element == null) {
				continue;
			}

			String name = clean(element.text());

			if (isValidSchoolName(name)) {
				return name;
			}
		}

		/*
		 * Fallback: look at links.
		 */
		for (Element link : listing.select("a[href]")) {

			String name = clean(link.text());

			if (isValidSchoolName(name)) {
				return name;
			}
		}

		return null;
	}

	private boolean isValidSchoolName(String value) {

		if (value == null || value.isBlank()) {

			return false;
		}

		if (value.length() < 3 || value.length() > 250) {

			return false;
		}

		String lower = value.toLowerCase();

		return !lower.equals("view") && !lower.equals("view all") && !lower.equals("click here")
				&& !lower.equals("whatsapp") && !lower.equals("enquiry") && !lower.equals("contact")
				&& !lower.equals("show number");
	}

	/**
	 * Extract address from listing.
	 */
	private String extractAddress(Element listing, String city) {

		String[] selectors = {

				".cont_fl_addr",

				".address",

				"[class*=address]",

				"[class*=addr]" };

		for (String selector : selectors) {

			Element element = listing.selectFirst(selector);

			if (element != null) {

				String address = clean(element.text());

				if (address != null && address.length() > 10) {

					return address;
				}
			}
		}

		/*
		 * Fallback: find text containing city.
		 */
		for (Element element : listing.select("div,span,p")) {

			String text = clean(element.text());

			if (text == null) {
				continue;
			}

			if (text.toLowerCase().contains(city.toLowerCase()) && text.length() < 500) {

				return text;
			}
		}

		return null;
	}

	/**
	 * Extract address from detail page.
	 */
	private String extractDetailAddress(Document document) {

		String[] selectors = {

				".cont_fl_addr",

				".address",

				".address-info",

				"[class*=address]",

				"[class*=addr]" };

		for (String selector : selectors) {

			Element element = document.selectFirst(selector);

			if (element == null) {
				continue;
			}

			String address = clean(element.text());

			if (address != null && address.length() > 10 && address.length() < 1000) {

				return address;
			}
		}

		return null;
	}

	/**
	 * Extract phone from listing.
	 */
	private String extractPhone(Element listing) {

		Element phoneElement = listing.selectFirst("a[href^=tel:]");

		if (phoneElement != null) {

			return clean(phoneElement.attr("href").replace("tel:", ""));
		}

		return null;
	}

	/**
	 * Extract phone from detail page.
	 */
	private String extractPhoneFromDetailPage(Document document) {

		Elements phoneLinks = document.select("a[href^=tel:]");

		for (Element link : phoneLinks) {

			String phone = clean(link.attr("href").replace("tel:", ""));

			if (phone != null) {
				return phone;
			}
		}

		return extractPhoneFromText(document.text());
	}

	/**
	 * Extract phone from plain text.
	 */
	private String extractPhoneFromText(String text) {

		if (text == null) {
			return null;
		}

		Matcher matcher = PHONE_PATTERN.matcher(text);

		if (matcher.find()) {

			String phone = matcher.group();

			return clean(phone.replaceAll("[^0-9+]", ""));
		}

		return null;
	}

	/**
	 * Extract email.
	 */
	private String extractEmail(Document document) {

		/*
		 * mailto first.
		 */
		Element mailto = document.selectFirst("a[href^=mailto:]");

		if (mailto != null) {

			String email = clean(mailto.attr("href").replace("mailto:", ""));

			if (email != null) {
				return email;
			}
		}

		/*
		 * Search page text.
		 */
		Matcher matcher = EMAIL_PATTERN.matcher(document.text());

		if (matcher.find()) {
			return matcher.group();
		}

		return null;
	}

	/**
	 * Extract external website.
	 */
	private String extractExternalWebsite(Document document) {

		for (Element link : document.select("a[href]")) {

			String href = link.attr("abs:href");

			if (href == null || href.isBlank()) {
				continue;
			}

			String lower = href.toLowerCase();

			if (!lower.startsWith("http")) {
				continue;
			}

			/*
			 * Ignore Justdial and social links.
			 */
			if (lower.contains("justdial.com")) {
				continue;
			}

			if (lower.contains("facebook.com")) {
				continue;
			}

			if (lower.contains("instagram.com")) {
				continue;
			}

			if (lower.contains("youtube.com")) {
				continue;
			}

			if (lower.contains("google.com")) {
				continue;
			}

			return href;
		}

		return null;
	}

	/**
	 * Extract Justdial listing URL.
	 */
	private String extractListingUrl(Element listing) {

		for (Element link : listing.select("a[href]")) {

			String href = link.attr("abs:href");

			if (href == null || href.isBlank()) {
				continue;
			}

			String lower = href.toLowerCase();

			if (!lower.contains("justdial.com")) {
				continue;
			}

			/*
			 * Ignore category/search pages.
			 */
			if (lower.contains("/schools")) {
				continue;
			}

			if (lower.contains("/search")) {
				continue;
			}

			return href;
		}

		return null;
	}

	/**
	 * Extract Justdial ID.
	 */
	private String extractJustdialId(Element listing, String url) {

		String id = clean(listing.attr("data-id"));

		if (id != null) {
			return id;
		}

		id = clean(listing.attr("data-identifier"));

		if (id != null) {
			return id;
		}

		/*
		 * Example URL:
		 *
		 * .../9999PX551-X551-150302174027-V2E1_BZDET
		 *
		 * Extract the identifier before _BZDET.
		 */
		if (url != null) {

			Matcher matcher = Pattern.compile("/([^/]+)_BZDET").matcher(url);

			if (matcher.find()) {
				return matcher.group(1);
			}
		}

		return null;
	}

	/**
	 * Extract rating.
	 */
	private Double extractRating(String text) {

		if (text == null) {
			return null;
		}

		Matcher matcher = RATING_PATTERN.matcher(text);

		while (matcher.find()) {

			try {

				double value = Double.parseDouble(matcher.group(1));

				if (value >= 0 && value <= 5) {

					return value;
				}

			} catch (Exception ignored) {
			}
		}

		return null;
	}

	/**
	 * Extract review count.
	 */
	private Integer extractReviewCount(String text) {

		if (text == null) {
			return null;
		}

		Matcher matcher = REVIEW_PATTERN.matcher(text);

		if (matcher.find()) {

			try {

				return Integer.parseInt(matcher.group(1).replace(",", ""));

			} catch (Exception ignored) {
			}
		}

		return null;
	}

	/**
	 * Extract board.
	 */
	private String extractBoard(String text) {

		if (text == null) {
			return null;
		}

		String upper = text.toUpperCase();

		if (upper.contains("CBSE")) {
			return "CBSE";
		}

		if (upper.contains("ICSE")) {
			return "ICSE";
		}

		if (upper.contains("ISC")) {
			return "ISC";
		}

		if (upper.contains("IB")) {
			return "IB";
		}

		if (upper.contains("UP BOARD")) {
			return "UP Board";
		}

		return null;
	}

	/**
	 * Extract school type.
	 */
	private String extractSchoolType(String text) {

		if (text == null) {
			return null;
		}

		String lower = text.toLowerCase();

		if (lower.contains("public school")) {
			return "Public School";
		}

		if (lower.contains("private school")) {
			return "Private School";
		}

		if (lower.contains("international school")) {
			return "International School";
		}

		if (lower.contains("convent school")) {
			return "Convent School";
		}

		return null;
	}

	/**
	 * Extract pincode.
	 */
	private String extractPincode(String address) {

		if (address == null) {
			return null;
		}

		Matcher matcher = PINCODE_PATTERN.matcher(address);

		if (matcher.find()) {
			return matcher.group();
		}

		return null;
	}

	/**
	 * Basic state extraction.
	 *
	 * For a production implementation, preferably maintain a state master.
	 */
	private String extractState(String address) {

		if (address == null) {
			return null;
		}

		String lower = address.toLowerCase();

		if (lower.contains("uttar pradesh")) {
			return "Uttar Pradesh";
		}

		if (lower.contains("karnataka")) {
			return "Karnataka";
		}

		if (lower.contains("maharashtra")) {
			return "Maharashtra";
		}

		if (lower.contains("delhi")) {
			return "Delhi";
		}

		if (lower.contains("bihar")) {
			return "Bihar";
		}

		if (lower.contains("rajasthan")) {
			return "Rajasthan";
		}

		if (lower.contains("madhya pradesh")) {
			return "Madhya Pradesh";
		}

		return null;
	}

	/**
	 * Extract locality from address.
	 */
	private String extractLocality(String address, String city) {

		if (address == null || address.isBlank()) {
			return null;
		}

		String value = address;

		if (city != null) {

			value = value.replaceAll("(?i)\\b" + Pattern.quote(city) + "\\b", "");
		}

		value = value.replaceAll("\\b\\d{6}\\b", "");

		value = value.replaceAll("\\s*,\\s*", ",");

		value = value.trim();

		if (value.isBlank()) {
			return null;
		}

		return value;
	}

	/**
	 * Save or update lead.
	 */
	private SchoolLead saveOrUpdateLead(LeadDto dto) {

		SchoolLead lead = null;
		/*
		 * Create new entity.
		 */
		if (lead == null) {

			lead = new SchoolLead();

			lead.setStatus(LeadStatus.NEW);
		}

		/*
		 * Set all fields.
		 */
		lead.setSchoolName(dto.getSchoolName());

		lead.setAddress(dto.getAddress());

		lead.setLocality(dto.getLocality());

		lead.setCity(dto.getCity());

		lead.setState(dto.getState());

		lead.setPincode(dto.getPincode());

		lead.setPhone(dto.getPhone());

		lead.setAlternatePhone(dto.getAlternatePhone());

		lead.setEmail(dto.getEmail());

		lead.setWebsite(dto.getWebsite());

		lead.setCategory(dto.getCategory());

		lead.setBoard(dto.getBoard());

		lead.setSchoolType(dto.getSchoolType());

		return schoolLeadRepository.save(lead);
	}

	/**
	 * Clean string.
	 */
	private String clean(String value) {

		if (value == null) {
			return null;
		}

		value = value.replaceAll("\\s+", " ").trim();

		return value.isBlank() ? null : value;
	}

	/**
	 * Sleep between detail requests.
	 */
	private void sleep(long milliseconds) {

		try {

			Thread.sleep(milliseconds);

		} catch (InterruptedException e) {

			Thread.currentThread().interrupt();
		}
	}

}