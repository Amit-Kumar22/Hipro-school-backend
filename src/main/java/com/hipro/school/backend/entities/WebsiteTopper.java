package com.hipro.school.backend.entities;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "website_toppers", indexes = { @Index(name = "idx_toppers_session", columnList = "session") })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebsiteTopper {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private Long id;

	@Column(name = "topper_rank", nullable = false)
	private Integer rank;

	@Column(nullable = false, length = 150)
	private String name;

	@Column(length = 500)
	private String photoUrl;

	@Column(nullable = false, precision = 5, scale = 2)
	private BigDecimal percentage;

	@Column(length = 100)
	private String stream;

	@Column(nullable = false, length = 30)
	private String session;

	@Column(nullable = false)
	@Builder.Default
	private Boolean active = true;
}
