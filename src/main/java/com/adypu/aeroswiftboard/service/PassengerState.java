package com.adypu.aeroswiftboard.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.adypu.aeroswiftboard.model.Passenger;

/**
 * Holds the passengers loaded for the current application instance.
 *
 * <p>The stored list is an immutable snapshot so callers cannot accidentally
 * replace or remove entries from the shared collection.</p>
 */
@Service
public class PassengerState {

	private static final Logger logger = LoggerFactory.getLogger(PassengerState.class);

	private volatile List<Passenger> passengers = List.of();

	/**
	 * Replaces the stored passengers with an immutable snapshot of the supplied list.
	 *
	 * @param loadedPassengers passengers loaded from the CSV resource
	 */
	public synchronized void setPassengers(List<Passenger> loadedPassengers) {
		passengers = List.copyOf(loadedPassengers);
		logger.info("Passenger list updated. Remaining passenger count={}", passengers.size());
	}

	/**
	 * Returns the passengers currently stored for this application instance.
	 *
	 * @return an immutable snapshot of the stored passenger list
	 */
	public synchronized List<Passenger> getPassengers() {
		return passengers;
	}

	/**
	 * Removes the passenger identified by boarding-pass number.
	 *
	 * @param boardingPassNumber unique boarding-pass number
	 * @return {@code true} if a passenger was removed; otherwise {@code false}
	 */
	public synchronized boolean removePassenger(String boardingPassNumber) {
		List<Passenger> updatedPassengers = new ArrayList<>(passengers);
		boolean removed = updatedPassengers.removeIf(
				passenger -> Objects.equals(passenger.getBoardingPassNumber(), boardingPassNumber));
		if (removed) {
			passengers = List.copyOf(updatedPassengers);
			logger.info("Passenger list updated. Remaining passenger count={}", passengers.size());
		}
		return removed;
	}
}
