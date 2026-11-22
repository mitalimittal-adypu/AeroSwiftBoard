package com.adypu.aeroswiftboard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import com.adypu.aeroswiftboard.comparator.DefaultBoardingPriorityComparator;
import com.adypu.aeroswiftboard.comparator.SeniorCitizenBoardingPriorityComparator;
import com.adypu.aeroswiftboard.comparator.SeniorCitizenVeteranBoardingPriorityComparator;
import com.adypu.aeroswiftboard.model.BoardingPrioritySelection;
import com.adypu.aeroswiftboard.model.Passenger;
import com.adypu.aeroswiftboard.service.BoardingPrioritySelectionState;
import com.adypu.aeroswiftboard.service.PassengerBoardingService;
import com.adypu.aeroswiftboard.service.PassengerCsvLoader;
import com.adypu.aeroswiftboard.service.PassengerState;

@SpringBootTest
@AutoConfigureMockMvc
class PassengerBoardingControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private PassengerCsvLoader csvLoader;

	@Autowired
	private PassengerState passengerState;

	@Autowired
	private BoardingPrioritySelectionState selectionState;

	@Autowired
	private PassengerBoardingService passengerBoardingService;

	@BeforeEach
	void loadCsvPassengers() throws IOException {
		passengerState.setPassengers(csvLoader.loadPassengers());
		selectionState.updateSelection(new BoardingPrioritySelection(false, false));
	}

	@AfterEach
	void restoreCsvPassengers() throws IOException {
		passengerState.setPassengers(csvLoader.loadPassengers());
		selectionState.updateSelection(new BoardingPrioritySelection(false, false));
	}

	@Test
	void returnsFivePassengersInDefaultPriorityOrderAndTotalCount() throws Exception {
		List<Passenger> expectedOrder = new ArrayList<>(passengerState.getPassengers());
		expectedOrder.sort(new DefaultBoardingPriorityComparator());

		mockMvc.perform(get("/api/passengers/boarding"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.passengers.length()").value(5))
				.andExpect(jsonPath("$.passengers[0].boardingPassNumber")
						.value(expectedOrder.get(0).getBoardingPassNumber()))
				.andExpect(jsonPath("$.remainingPassengerCount").value(200));
	}

	@Test
	void choosesComparatorForEachPrioritySelection() {
		assertQueueUsesComparator(false, false, new DefaultBoardingPriorityComparator());
		assertQueueUsesComparator(true, false, new SeniorCitizenBoardingPriorityComparator());
		assertQueueUsesComparator(true, true, new SeniorCitizenVeteranBoardingPriorityComparator());
		assertQueueUsesComparator(false, true, new SeniorCitizenVeteranBoardingPriorityComparator());
	}

	@Test
	void removesBoardedPassengerAndReturnsUpdatedQueueCount() throws Exception {
		List<Passenger> expectedOrder = new ArrayList<>(passengerState.getPassengers());
		expectedOrder.sort(new DefaultBoardingPriorityComparator());
		String boardedPassNumber = expectedOrder.get(0).getBoardingPassNumber();

		mockMvc.perform(post("/api/passengers/{boardingPassNumber}/board", boardedPassNumber))
				.andExpect(status().isNoContent());

		assertEquals(199, passengerState.getPassengers().size());
		assertTrue(passengerState.getPassengers().stream()
				.noneMatch(passenger -> passenger.getBoardingPassNumber().equals(boardedPassNumber)));
		mockMvc.perform(get("/api/passengers/boarding"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.remainingPassengerCount").value(199));
	}

	@Test
	void rejectsNonPositiveQueueLimit() throws Exception {
		mockMvc.perform(get("/api/passengers/boarding").param("limit", "0"))
				.andExpect(status().isBadRequest());
	}

	private void assertQueueUsesComparator(boolean seniorCitizen, boolean veteran,
			Comparator<Passenger> comparator) {
		selectionState.updateSelection(new BoardingPrioritySelection(seniorCitizen, veteran));
		List<Passenger> expectedOrder = new ArrayList<>(passengerState.getPassengers());
		expectedOrder.sort(comparator);

		assertEquals(expectedOrder.subList(0, 5).stream()
				.map(Passenger::getBoardingPassNumber).toList(),
				passengerBoardingService.getNextPassengers(5).passengers().stream()
						.map(Passenger::getBoardingPassNumber).toList());
	}
}
