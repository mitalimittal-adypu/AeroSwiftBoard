package com.adypu.aeroswiftboard.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.adypu.aeroswiftboard.model.BoardingQueueResponse;
import com.adypu.aeroswiftboard.service.PassengerBoardingService;

/**
 * API for displaying the next passengers and recording completed boarding.
 */
@RestController
@RequestMapping("/api/passengers")
public class PassengerBoardingController {

	private final PassengerBoardingService passengerBoardingService;

	/**
	 * Creates the controller with the passenger boarding service.
	 *
	 * @param passengerBoardingService service for queue retrieval and boarding
	 */
	public PassengerBoardingController(PassengerBoardingService passengerBoardingService) {
		this.passengerBoardingService = passengerBoardingService;
	}

	/**
	 * Gets the next passengers in default priority order.
	 *
	 * @param limit maximum passengers to return; defaults to five
	 * @return next passengers and total passengers remaining in the queue
	 */
	@GetMapping("/boarding")
	public BoardingQueueResponse getNextPassengers(@RequestParam(defaultValue = "5") int limit) {
		if (limit < 1) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "limit must be greater than zero");
		}
		return passengerBoardingService.getNextPassengers(limit);
	}

	/**
	 * Removes a passenger from the queue after they board.
	 *
	 * @param boardingPassNumber boarding-pass number of the passenger to remove
	 * @return {@code 204 No Content} when removed
	 */
	@PostMapping("/{boardingPassNumber}/board")
	public ResponseEntity<Void> boardPassenger(@PathVariable String boardingPassNumber) {
		if (!passengerBoardingService.boardPassenger(boardingPassNumber)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Passenger not found in the boarding queue");
		}
		return ResponseEntity.noContent().build();
	}
}
