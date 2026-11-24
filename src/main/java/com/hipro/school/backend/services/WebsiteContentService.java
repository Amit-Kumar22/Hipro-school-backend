package com.hipro.school.backend.services;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hipro.school.backend.dtos.WebsiteContentResponse;
import com.hipro.school.backend.entities.Holiday;
import com.hipro.school.backend.entities.WebsiteFeature;
import com.hipro.school.backend.entities.WebsiteGalleryItem;
import com.hipro.school.backend.entities.WebsiteHeroButton;
import com.hipro.school.backend.entities.WebsiteHeroSlide;
import com.hipro.school.backend.entities.WebsiteNavigationItem;
import com.hipro.school.backend.entities.WebsiteSection;
import com.hipro.school.backend.entities.WebsiteSocialLink;
import com.hipro.school.backend.entities.WebsiteStatistic;
import com.hipro.school.backend.entities.WebsiteTestimonial;
import com.hipro.school.backend.entities.WebsiteTopper;
import com.hipro.school.backend.entities.WebsiteWhyChoosePoint;
import com.hipro.school.backend.repositories.HolidayRepository;
import com.hipro.school.backend.repositories.WebsiteContactRepository;
import com.hipro.school.backend.repositories.WebsiteFeatureRepository;
import com.hipro.school.backend.repositories.WebsiteFloatingSettingRepository;
import com.hipro.school.backend.repositories.WebsiteGalleryItemRepository;
import com.hipro.school.backend.repositories.WebsiteHeaderRepository;
import com.hipro.school.backend.repositories.WebsiteHeroButtonRepository;
import com.hipro.school.backend.repositories.WebsiteHeroSlideRepository;
import com.hipro.school.backend.repositories.WebsiteNavigationItemRepository;
import com.hipro.school.backend.repositories.WebsiteSectionRepository;
import com.hipro.school.backend.repositories.WebsiteSettingRepository;
import com.hipro.school.backend.repositories.WebsiteSocialLinkRepository;
import com.hipro.school.backend.repositories.WebsiteStatisticRepository;
import com.hipro.school.backend.repositories.WebsiteTestimonialRepository;
import com.hipro.school.backend.repositories.WebsiteTopperRepository;
import com.hipro.school.backend.repositories.WebsiteWhyChoosePointRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WebsiteContentService {

	@Autowired
	private WebsiteSettingRepository websiteSettingRepository;

	@Autowired
	private WebsiteContactRepository websiteContactRepository;

	@Autowired
	private WebsiteHeaderRepository websiteHeaderRepository;

	@Autowired
	private WebsiteFloatingSettingRepository websiteFloatingSettingRepository;

	@Autowired
	private WebsiteSectionRepository websiteSectionRepository;

	@Autowired
	private WebsiteSocialLinkRepository websiteSocialLinkRepository;

	@Autowired
	private WebsiteNavigationItemRepository websiteNavigationItemRepository;

	@Autowired
	private WebsiteHeroSlideRepository websiteHeroSlideRepository;

	@Autowired
	private WebsiteHeroButtonRepository websiteHeroButtonRepository;

	@Autowired
	private WebsiteFeatureRepository websiteFeatureRepository;

	@Autowired
	private WebsiteGalleryItemRepository websiteGalleryItemRepository;

	@Autowired
	private WebsiteTestimonialRepository websiteTestimonialRepository;

	@Autowired
	private WebsiteStatisticRepository websiteStatisticRepository;

	@Autowired
	private WebsiteTopperRepository websiteTopperRepository;

	@Autowired
	private WebsiteWhyChoosePointRepository websiteWhyChoosePointRepository;

	@Autowired
	private HolidayRepository holidayRepository;

	@Transactional(readOnly = true)
	public WebsiteContentResponse getWebsiteContent() {
		log.info(">> getWebsiteContent()");

		List<WebsiteSocialLink> socialLinks = ordered(websiteSocialLinkRepository.findAll(),
				WebsiteSocialLink::getDisplayOrder);

		List<WebsiteNavigationItem> navigationItems = ordered(websiteNavigationItemRepository.findAll(),
				WebsiteNavigationItem::getDisplayOrder);

		List<WebsiteHeroSlide> heroSlides = ordered(websiteHeroSlideRepository.findAll(),
				WebsiteHeroSlide::getDisplayOrder);

		List<WebsiteHeroButton> heroButtons = ordered(websiteHeroButtonRepository.findAll(),
				WebsiteHeroButton::getDisplayOrder);

		List<WebsiteFeature> features = ordered(websiteFeatureRepository.findAll(), WebsiteFeature::getDisplayOrder);

		List<WebsiteGalleryItem> galleryItems = ordered(websiteGalleryItemRepository.findAll(),
				WebsiteGalleryItem::getDisplayOrder);

		List<WebsiteTestimonial> testimonials = ordered(websiteTestimonialRepository.findAll(),
				WebsiteTestimonial::getDisplayOrder);

		List<WebsiteStatistic> statistics = ordered(websiteStatisticRepository.findAll(),
				WebsiteStatistic::getDisplayOrder);

		List<WebsiteTopper> toppers = ordered(websiteTopperRepository.findAll(), WebsiteTopper::getRank);

		List<WebsiteWhyChoosePoint> whyChoosePoints = ordered(websiteWhyChoosePointRepository.findAll(),
				WebsiteWhyChoosePoint::getDisplayOrder);

		Optional<Holiday> holiday = holidayRepository.findByHolidayDate(LocalDate.now());
		Holiday oh = null;
		if (holiday.isPresent()) {
			oh = holiday.get();
		}

		return WebsiteContentResponse.builder()
				.setting(websiteSettingRepository.findAll().stream().findFirst().orElse(null))
				.contact(websiteContactRepository.findAll().stream().findFirst().orElse(null))
				.header(websiteHeaderRepository.findAll().stream().findFirst().orElse(null))
				.floatingSetting(websiteFloatingSettingRepository.findAll().stream().findFirst().orElse(null))
				.sections(ordered(websiteSectionRepository.findAll(), WebsiteSection::getDisplayOrder))
				.socialLinks(socialLinks).navigationItems(navigationItems).heroSlides(heroSlides)
				.heroButtons(heroButtons).features(features).galleryItems(galleryItems).testimonials(testimonials)
				.holiday(oh)
				.statistics(statistics).toppers(toppers).whyChoosePoints(whyChoosePoints).build();
	}

	private <T> List<T> ordered(List<T> items, Function<T, Integer> orderGetter) {
		return items.stream().sorted(orderComparator(orderGetter)).toList();
	}

	private <T> Comparator<T> orderComparator(Function<T, Integer> orderGetter) {
		return (first, second) -> {
			Integer left = orderGetter.apply(first);
			Integer right = orderGetter.apply(second);
			int leftOrder = left != null ? left : Integer.MAX_VALUE;
			int rightOrder = right != null ? right : Integer.MAX_VALUE;
			return Integer.compare(leftOrder, rightOrder);
		};
	}

}
