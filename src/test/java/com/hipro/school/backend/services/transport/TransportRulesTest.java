package com.hipro.school.backend.services.transport;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.hipro.school.backend.enums.TransportDesignation;

class TransportRulesTest {

	@Test
	@DisplayName("Licence expiring before today is expired")
	void shouldDetectExpiredLicence() {
		assertTrue(TransportRules.isLicenceExpired(LocalDate.now().minusDays(1), LocalDate.now()));
	}

	@Test
	@DisplayName("Licence expiring today or later is valid; missing expiry is valid")
	void shouldAcceptValidLicence() {
		assertFalse(TransportRules.isLicenceExpired(LocalDate.now(), LocalDate.now()));
		assertFalse(TransportRules.isLicenceExpired(LocalDate.now().plusDays(30), LocalDate.now()));
		assertFalse(TransportRules.isLicenceExpired(null, LocalDate.now()));
	}

	@Test
	@DisplayName("Only driver designations require a valid licence")
	void shouldIdentifyDriverDesignations() {
		assertTrue(TransportRules.isDriverDesignation(TransportDesignation.BUS_DRIVER));
		assertTrue(TransportRules.isDriverDesignation(TransportDesignation.TRAVELLER_DRIVER));
		assertTrue(TransportRules.isDriverDesignation(TransportDesignation.VAN_DRIVER));
		assertFalse(TransportRules.isDriverDesignation(TransportDesignation.CONDUCTOR));
		assertFalse(TransportRules.isDriverDesignation(TransportDesignation.TRANSPORT_IN_CHARGE));
		assertFalse(TransportRules.isDriverDesignation(TransportDesignation.HELPER));
	}

	@Test
	@DisplayName("Seat is available only while occupied seats are below capacity")
	void shouldEnforceSeatingCapacity() {
		assertTrue(TransportRules.hasSeatingCapacity(40, 39));
		assertFalse(TransportRules.hasSeatingCapacity(40, 40));
		assertFalse(TransportRules.hasSeatingCapacity(40, 41));
		assertFalse(TransportRules.hasSeatingCapacity(null, 0));
	}
}
