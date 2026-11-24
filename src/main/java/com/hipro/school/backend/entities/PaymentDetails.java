package com.hipro.school.backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hipro.school.backend.enums.PaymentStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDetails extends BaseEntity {

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id", nullable = false)
	@JsonIgnore
	private User user;

	private String description;

	private String reason;

	private String paymentId;

	private String razorpayOrderId;

	private Double discountAmount;

	@Column(name = "bank_response", columnDefinition = "TEXT")
	private String bankResponse;

	@Column(name = "refund_detail", columnDefinition = "TEXT")
	private String refundDetails;

	@Enumerated(EnumType.STRING)
	@Column(name = "payment_status", nullable = false)
	private PaymentStatus paymentStatus;

}
