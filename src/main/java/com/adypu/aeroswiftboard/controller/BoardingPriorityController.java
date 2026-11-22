package com.adypu.aeroswiftboard.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.adypu.aeroswiftboard.model.BoardingPrioritySelection;
import com.adypu.aeroswiftboard.service.BoardingPrioritySelectionState;

/**
 * Accepts and stores boarding-priority preferences submitted by the UI.
 *
 * <p>This controller stores selection state only; it does not decide passenger
 * boarding order.</p>
 */
@RestController
@RequestMapping("/api/boarding-priority")
public class BoardingPriorityController {

	private final BoardingPrioritySelectionState selectionState;

	/**
	 * Creates the controller with the application-wide selection state.
	 *
	 * @param selectionState singleton service used to store UI preferences
	 */
	public BoardingPriorityController(BoardingPrioritySelectionState selectionState) {
		this.selectionState = selectionState;
	}

	/**
	 * Stores the senior-citizen and veteran selections received from the UI.
	 *
	 * @param selection request body containing both preference values
	 * @return the stored selection
	 * @throws ResponseStatusException if either preference value is missing
	 */
	@PostMapping
	public BoardingPrioritySelection updateSelection(@RequestBody BoardingPrioritySelection selection) {
		if (selection.seniorCitizen() == null || selection.veteran() == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Both seniorCitizen and veteran selections are required");
		}

		return selectionState.updateSelection(selection);
	}
}
