package com.adypu.aeroswiftboard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.adypu.aeroswiftboard.service.PassengerState;

@SpringBootTest
class AeroSwiftBoardApplicationTests {

	@Autowired
	private PassengerState passengerState;

	@Test
	void contextLoads() {
	}

	@Test
	void loadsPassengersIntoSingletonStateAtStartup() {
		assertEquals(200, passengerState.getPassengers().size());
		assertThrows(UnsupportedOperationException.class, () -> passengerState.getPassengers().clear());
	}

}
