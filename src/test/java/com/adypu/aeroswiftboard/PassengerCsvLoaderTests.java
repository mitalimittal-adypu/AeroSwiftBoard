package com.adypu.aeroswiftboard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.adypu.aeroswiftboard.model.Passenger;
import com.adypu.aeroswiftboard.service.PassengerCsvLoader;

class PassengerCsvLoaderTests {

	private final PassengerCsvLoader loader = new PassengerCsvLoader();

	@Test
	void loadsPassengersFromClasspathCsv() throws IOException {
		List<Passenger> passengers = loader.loadPassengers();

		assertEquals(200, passengers.size());

		Passenger firstPassenger = passengers.get(0);
		assertEquals("Mei Khan", firstPassenger.getPassengerName());
		assertEquals("BP2263132191", firstPassenger.getBoardingPassNumber());
		assertEquals("16C", firstPassenger.getSeatNumber());
		assertEquals("C", firstPassenger.getZone());
		assertEquals(11, firstPassenger.getAge());
		assertFalse(firstPassenger.isVeteran());
		assertTrue(passengers.stream().anyMatch(Passenger::isVeteran));
	}
}
