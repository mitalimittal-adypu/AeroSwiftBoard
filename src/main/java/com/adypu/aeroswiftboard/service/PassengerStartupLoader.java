package com.adypu.aeroswiftboard.service;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.adypu.aeroswiftboard.model.Passenger;

/**
 * Loads passengers from the classpath CSV after the Spring application starts.
 */
@Component
public class PassengerStartupLoader implements ApplicationRunner {

	private static final Logger logger = LoggerFactory.getLogger(PassengerStartupLoader.class);

	private final PassengerCsvLoader csvLoader;
	private final PassengerState passengerState;

	/**
	 * Creates the startup loader with the CSV reader and shared passenger state.
	 *
	 * @param csvLoader reads and parses passengers.csv
	 * @param passengerState stores the loaded passengers for the application
	 */
	public PassengerStartupLoader(PassengerCsvLoader csvLoader, PassengerState passengerState) {
		this.csvLoader = csvLoader;
		this.passengerState = passengerState;
	}

	/**
	 * Loads the complete passenger list into the singleton state and logs its size.
	 *
	 * @param args command-line arguments supplied at application startup
	 * @throws IOException if the CSV cannot be read or contains invalid data
	 */
	@Override
	public void run(ApplicationArguments args) throws IOException {
		List<Passenger> passengers = csvLoader.loadPassengers();
		passengerState.setPassengers(passengers);
		logger.info("Loaded {} passengers from passengers.csv", passengers.size());
	}
}
