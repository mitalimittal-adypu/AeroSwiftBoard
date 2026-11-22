package com.adypu.aeroswiftboard.comparator;

import java.util.Comparator;

import com.adypu.aeroswiftboard.model.Passenger;

public class SeniorCitizenVeteranBoardingPriorityComparator implements Comparator<Passenger> {

	private final DefaultBoardingPriorityComparator defaultBoardingPriorityComparator =
			new DefaultBoardingPriorityComparator();

	@Override
	public int compare(Passenger first, Passenger second) {
		int seniorPriority = Boolean.compare(second.getAge() >= 58, first.getAge() >= 58);
		if (seniorPriority != 0) {
			return seniorPriority;
		}

		int veteranPriority = Boolean.compare(second.isVeteran(), first.isVeteran());
		if (veteranPriority != 0) {
			return veteranPriority;
		}

		return defaultBoardingPriorityComparator.compare(first, second);
	}
}
