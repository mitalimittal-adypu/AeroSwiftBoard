package com.adypu.aeroswiftboard.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.adypu.aeroswiftboard.comparator.DefaultBoardingPriorityComparator;
import com.adypu.aeroswiftboard.comparator.SeniorCitizenBoardingPriorityComparator;
import com.adypu.aeroswiftboard.comparator.SeniorCitizenVeteranBoardingPriorityComparator;
import com.adypu.aeroswiftboard.model.BoardingQueueResponse;
import com.adypu.aeroswiftboard.model.BoardingPrioritySelection;
import com.adypu.aeroswiftboard.model.Passenger;

/**
 * Provides the ordered boarding queue and removes passengers after boarding.
 */
@Service
public class PassengerBoardingService {

	private final PassengerState passengerState;
	private final BoardingPrioritySelectionState selectionState;
	private final Comparator<Passenger> defaultBoardingPriorityComparator = new DefaultBoardingPriorityComparator();
	private final Comparator<Passenger> seniorCitizenBoardingPriorityComparator =
			new SeniorCitizenBoardingPriorityComparator();
	private final Comparator<Passenger> seniorCitizenVeteranBoardingPriorityComparator =
			new SeniorCitizenVeteranBoardingPriorityComparator();

	/**
	 * Creates the boarding service backed by the shared passenger and selection states.
	 *
	 * @param passengerState singleton passenger collection
	 * @param selectionState singleton UI priority selection
	 */
	public PassengerBoardingService(PassengerState passengerState,
			BoardingPrioritySelectionState selectionState) {
		this.passengerState = passengerState;
		this.selectionState = selectionState;
	}

	/**
	 * Returns up to the requested number of passengers using the comparator selected
	 * by the current senior-citizen and veteran preferences.
	 *
	 * @param limit maximum number of passengers to return
	 * @return ordered passengers and the total number remaining in the queue
	 */
	public BoardingQueueResponse getNextPassengers(int limit) {
		List<Passenger> remainingPassengers = passengerState.getPassengers();
		Comparator<Passenger> boardingPriorityComparator =
				getBoardingPriorityComparator(selectionState.getCurrentSelection());
		List<Passenger> nextPassengers = remainingPassengers.stream()
				.sorted(boardingPriorityComparator)
				.limit(limit)
				.toList();
		return new BoardingQueueResponse(nextPassengers, remainingPassengers.size());
	}

	/**
	 * Selects the comparator matching the active UI preferences. Veteran priority
	 * uses the combined comparator whether or not senior-citizen priority is selected.
	 *
	 * @param selection current singleton preference state
	 * @return comparator for the selected priority criteria
	 */
	private Comparator<Passenger> getBoardingPriorityComparator(BoardingPrioritySelection selection) {
		if (selection.veteran()) {
			return seniorCitizenVeteranBoardingPriorityComparator;
		}
		if (selection.seniorCitizen()) {
			return seniorCitizenBoardingPriorityComparator;
		}
		return defaultBoardingPriorityComparator;
	}

	/**
	 * Marks a passenger as boarded by removing them from the shared queue.
	 *
	 * @param boardingPassNumber boarding-pass number of the passenger
	 * @return {@code true} if the passenger was found and removed
	 */
	public boolean boardPassenger(String boardingPassNumber) {
		return passengerState.removePassenger(boardingPassNumber);
	}
}
