package com.hipro.school.backend.entities;

import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "periods", indexes = { @Index(name = "idx_period_order", columnList = "period_order") })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Period extends BaseEntity {

	@NotBlank
	@Size(max = 100)
	@Column(nullable = false, length = 100)
	private String name;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "class_id", nullable = false)
	private SchoolClass schoolClass;

	@NotNull
	@Positive
	@Column(name = "period_order", nullable = false)
	private Integer periodOrder;

	@NotNull
	@Column(name = "start_time", nullable = false)
	private LocalTime startTime;

	@NotNull
	@Column(name = "end_time", nullable = false)
	private LocalTime endTime;

	@Builder.Default
	@Column(nullable = false)
	private Boolean breakPeriod = false;

}