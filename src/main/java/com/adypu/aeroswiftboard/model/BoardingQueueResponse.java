package com.adypu.aeroswiftboard.model;

import java.util.List;

/**
 * Contains the next passengers to board and the total remaining queue size.
 *
 * @param passengers next passengers ordered for boarding
 * @param remainingPassengerCount total passengers still in the queue
 */
public record BoardingQueueResponse(List<Passenger> passengers, int remainingPassengerCount) {

	public BoardingQueueResponse {
		passengers = List.copyOf(passengers);
	}
}
