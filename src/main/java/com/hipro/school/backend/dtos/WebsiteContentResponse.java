package com.hipro.school.backend.dtos;

import java.util.List;

import com.hipro.school.backend.entities.Holiday;
import com.hipro.school.backend.entities.WebsiteContact;
import com.hipro.school.backend.entities.WebsiteFeature;
import com.hipro.school.backend.entities.WebsiteFloatingSetting;
import com.hipro.school.backend.entities.WebsiteGalleryItem;
import com.hipro.school.backend.entities.WebsiteHeader;
import com.hipro.school.backend.entities.WebsiteHeroButton;
import com.hipro.school.backend.entities.WebsiteHeroSlide;
import com.hipro.school.backend.entities.WebsiteNavigationItem;
import com.hipro.school.backend.entities.WebsiteSection;
import com.hipro.school.backend.entities.WebsiteSetting;
import com.hipro.school.backend.entities.WebsiteSocialLink;
import com.hipro.school.backend.entities.WebsiteStatistic;
import com.hipro.school.backend.entities.WebsiteTestimonial;
import com.hipro.school.backend.entities.WebsiteTopper;
import com.hipro.school.backend.entities.WebsiteWhyChoosePoint;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebsiteContentResponse {

	private WebsiteSetting setting;

	private WebsiteContact contact;

	private WebsiteHeader header;
	
	private Holiday holiday;

	private WebsiteFloatingSetting floatingSetting;

	private List<WebsiteSection> sections;

	private List<WebsiteSocialLink> socialLinks;

	private List<WebsiteNavigationItem> navigationItems;

	private List<WebsiteHeroSlide> heroSlides;

	private List<WebsiteHeroButton> heroButtons;

	private List<WebsiteFeature> features;

	private List<WebsiteGalleryItem> galleryItems;

	private List<WebsiteTestimonial> testimonials;

	private List<WebsiteStatistic> statistics;

	private List<WebsiteTopper> toppers;

	private List<WebsiteWhyChoosePoint> whyChoosePoints;

}
