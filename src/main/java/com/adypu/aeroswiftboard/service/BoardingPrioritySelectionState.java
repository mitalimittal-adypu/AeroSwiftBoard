package com.adypu.aeroswiftboard.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.adypu.aeroswiftboard.model.BoardingPrioritySelection;

/**
 * Stores the latest UI boarding-priority selections for this application instance.
 *
 * <p>This Spring singleton keeps selections in memory only. Its initial state has
 * neither optional preference selected; it does not perform passenger sorting.</p>
 */
@Service
public class BoardingPrioritySelectionState {

	private static final Logger logger = LoggerFactory.getLogger(BoardingPrioritySelectionState.class);

	/**
	 * The latest submitted selection. Volatile makes updates visible across request
	 * threads; the selection record itself is immutable.
	 */
	private volatile BoardingPrioritySelection currentSelection = new BoardingPrioritySelection(false, false);

	/**
	 * Replaces the stored selection with the latest UI values.
	 *
	 * @param selection the non-null selection received from the API
	 * @return the newly stored selection
	 */
	public synchronized BoardingPrioritySelection updateSelection(BoardingPrioritySelection selection) {
		if (!selection.equals(currentSelection)) {
			currentSelection = selection;
			logger.info("Boarding priority selection changed: seniorCitizen={}, veteran={}",
					selection.seniorCitizen(), selection.veteran());
		}
		return currentSelection;
	}

	/**
	 * Returns the selection currently stored in this application instance.
	 *
	 * @return the latest submitted selection, or the default selection if none was submitted
	 */
	public BoardingPrioritySelection getCurrentSelection() {
		return currentSelection;
	}
}
