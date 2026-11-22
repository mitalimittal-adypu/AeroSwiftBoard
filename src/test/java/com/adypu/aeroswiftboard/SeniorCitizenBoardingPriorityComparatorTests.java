package com.adypu.aeroswiftboard;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.adypu.aeroswiftboard.comparator.SeniorCitizenBoardingPriorityComparator;
import com.adypu.aeroswiftboard.model.Passenger;

class SeniorCitizenBoardingPriorityComparatorTests {

	private final SeniorCitizenBoardingPriorityComparator comparator =
			new SeniorCitizenBoardingPriorityComparator();

	@Test
	void prioritizesSeniorsThenBusinessClassAndZones() {
		List<Passenger> passengers = new ArrayList<>(List.of(
				passenger("Underage Business", 57, "Business_Class"),
				passenger("Senior Zone A", 58, "A"),
				passenger("Senior Zone C", 60, "C"),
				passenger("Senior Business", 58, "Business_Class"),
				passenger("Senior Zone B", 59, "B"),
				passenger("Underage Zone C", 57, "C")));

		passengers.sort(comparator);

		assertEquals(List.of(
				"Senior Business",
				"Senior Zone C",
				"Senior Zone B",
				"Senior Zone A",
				"Underage Business",
				"Underage Zone C"),
				passengers.stream().map(Passenger::getPassengerName).toList());
	}

	private Passenger passenger(String name, int age, String zone) {
		return new Passenger(name, "BP123", "1A", zone, age, false);
	}
}
