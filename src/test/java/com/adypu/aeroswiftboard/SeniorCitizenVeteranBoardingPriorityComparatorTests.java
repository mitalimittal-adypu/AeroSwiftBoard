package com.adypu.aeroswiftboard;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.adypu.aeroswiftboard.comparator.SeniorCitizenVeteranBoardingPriorityComparator;
import com.adypu.aeroswiftboard.model.Passenger;

class SeniorCitizenVeteranBoardingPriorityComparatorTests {

	private final SeniorCitizenVeteranBoardingPriorityComparator comparator =
			new SeniorCitizenVeteranBoardingPriorityComparator();

	@Test
	void prioritizesSeniorsThenVeteransThenBusinessClassAndZones() {
		List<Passenger> passengers = new ArrayList<>(List.of(
				passenger("Underage Veteran Business", 57, true, "Business_Class"),
				passenger("Senior Non-veteran Business", 58, false, "Business_Class"),
				passenger("Senior Veteran Zone A", 60, true, "A"),
				passenger("Senior Veteran Zone C", 60, true, "C"),
				passenger("Senior Veteran Business", 58, true, "Business_Class"),
				passenger("Underage Non-veteran Zone C", 57, false, "C")));

		passengers.sort(comparator);

		assertEquals(List.of(
				"Senior Veteran Business",
				"Senior Veteran Zone C",
				"Senior Veteran Zone A",
				"Senior Non-veteran Business",
				"Underage Veteran Business",
				"Underage Non-veteran Zone C"),
				passengers.stream().map(Passenger::getPassengerName).toList());
	}

	private Passenger passenger(String name, int age, boolean veteran, String zone) {
		return new Passenger(name, "BP123", "1A", zone, age, veteran);
	}
}
