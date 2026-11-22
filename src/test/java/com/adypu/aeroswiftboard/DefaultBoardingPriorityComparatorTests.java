package com.adypu.aeroswiftboard;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.adypu.aeroswiftboard.comparator.DefaultBoardingPriorityComparator;
import com.adypu.aeroswiftboard.model.Passenger;

class DefaultBoardingPriorityComparatorTests {

	private final DefaultBoardingPriorityComparator comparator = new DefaultBoardingPriorityComparator();

	@Test
	void sortsPassengersByBusinessClassThenZoneCBA() {
		List<Passenger> passengers = new ArrayList<>(List.of(
				passenger("Zone A", "A"),
				passenger("Zone B", "B"),
				passenger("Zone C", "C"),
				passenger("Business", "Business_Class")));

		passengers.sort(comparator);

		assertEquals(List.of("Business", "Zone C", "Zone B", "Zone A"),
				passengers.stream().map(Passenger::getPassengerName).toList());
	}

	@Test
	void keepsPassengersWithEqualPriorityTied() {
		assertEquals(0, comparator.compare(passenger("First C", "C"), passenger("Second C", "C")));
	}

	private Passenger passenger(String name, String zone) {
		return new Passenger(name, "BP123", "1A", zone, 30, false);
	}
}
