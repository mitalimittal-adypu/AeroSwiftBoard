package com.adypu.aeroswiftboard.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.adypu.aeroswiftboard.model.Passenger;

@Service
public class PassengerCsvLoader {

	private static final String RESOURCE_PATH = "passengers.csv";
	private static final String[] EXPECTED_HEADERS = {
			"Passanger_Name", "Boarding_Pass_Number", "seat_number", "zone", "Age", "Veteran"
	};

	public List<Passenger> loadPassengers() throws IOException {
		ClassPathResource resource = new ClassPathResource(RESOURCE_PATH);
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
			String headerLine = reader.readLine();
			if (headerLine == null || !Arrays.equals(headerLine.split(",", -1), EXPECTED_HEADERS)) {
				throw new IOException("Invalid or missing header in " + RESOURCE_PATH);
			}

			List<Passenger> passengers = new ArrayList<>();
			String line;
			int lineNumber = 1;
			while ((line = reader.readLine()) != null) {
				lineNumber++;
				if (line.isBlank()) {
					continue;
				}

				String[] fields = line.split(",", -1);
				if (fields.length != EXPECTED_HEADERS.length) {
					throw new IOException("Invalid column count in " + RESOURCE_PATH + " at line " + lineNumber);
				}

				try {
					passengers.add(new Passenger(
							fields[0].trim(),
							fields[1].trim(),
							fields[2].trim(),
							fields[3].trim(),
							Integer.parseInt(fields[4].trim()),
							parseVeteran(fields[5].trim(), lineNumber)));
				} catch (NumberFormatException exception) {
					throw new IOException("Invalid age in " + RESOURCE_PATH + " at line " + lineNumber, exception);
				}
			}

			return passengers;
		}
	}

	private boolean parseVeteran(String value, int lineNumber) throws IOException {
		if ("yes".equalsIgnoreCase(value)) {
			return true;
		}
		if ("no".equalsIgnoreCase(value)) {
			return false;
		}
		throw new IOException("Invalid veteran status in " + RESOURCE_PATH + " at line " + lineNumber);
	}
}
