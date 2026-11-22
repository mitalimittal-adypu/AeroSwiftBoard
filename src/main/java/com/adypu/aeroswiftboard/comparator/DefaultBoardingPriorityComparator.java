package com.adypu.aeroswiftboard.comparator;

import java.util.Comparator;

import com.adypu.aeroswiftboard.model.Passenger;

public class DefaultBoardingPriorityComparator implements Comparator<Passenger> {

	@Override
	public int compare(Passenger first, Passenger second) {
		return Integer.compare(priorityRank(first), priorityRank(second));
	}

	private int priorityRank(Passenger passenger) {
		String zone = passenger.getZone();
		if (zone == null) {
			return 4;
		}

		return switch (zone.trim().toUpperCase()) {
			case "BUSINESS_CLASS" -> 0;
			case "C" -> 1;
			case "B" -> 2;
			case "A" -> 3;
			default -> 4;
		};
	}
}
