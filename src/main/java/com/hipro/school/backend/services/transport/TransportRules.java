package com.hipro.school.backend.services.transport;

import java.time.LocalDate;

import com.hipro.school.backend.enums.TransportDesignation;

/**
 * Pure, framework-free transport business rules. Kept static so the rules are
 * unit-testable without a Spring context and reusable across services.
 */
public final class TransportRules {

	private TransportRules() {
	}

	/** A licence is expired when an expiry date exists and lies before today. */
	public static boolean isLicenceExpired(LocalDate licenceExpiry, LocalDate today) {
		if (licenceExpiry == null) {
			return false;
		}
		return licenceExpiry.isBefore(today);
	}

	public static boolean isDriverDesignation(TransportDesignation designation) {
		return designation == TransportDesignation.BUS_DRIVER
				|| designation == TransportDesignation.TRAVELLER_DRIVER
				|| designation == TransportDesignation.VAN_DRIVER;
	}

	/** Capacity is available when occupied seats are strictly below seating capacity. */
	public static boolean hasSeatingCapacity(Integer seatingCapacity, long occupiedSeats) {
		if (seatingCapacity == null) {
			return false;
		}
		return occupiedSeats < seatingCapacity;
	}
}
