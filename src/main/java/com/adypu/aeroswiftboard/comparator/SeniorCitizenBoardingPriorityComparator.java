package com.adypu.aeroswiftboard.comparator;

import java.util.Comparator;

import com.adypu.aeroswiftboard.model.Passenger;

public class SeniorCitizenBoardingPriorityComparator implements Comparator<Passenger> {

	private final DefaultBoardingPriorityComparator defaultBoardingPriorityComparator =
			new DefaultBoardingPriorityComparator();

	@Override
	public int compare(Passenger first, Passenger second) {
		int seniorPriority = Boolean.compare(second.getAge() >= 58, first.getAge() >= 58);
		if (seniorPriority != 0) {
			return seniorPriority;
		}

		return defaultBoardingPriorityComparator.compare(first, second);
	}
}
