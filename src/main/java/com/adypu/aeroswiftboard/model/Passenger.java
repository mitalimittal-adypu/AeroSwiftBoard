package com.adypu.aeroswiftboard.model;

public class Passenger {

	private String passengerName;
	private String boardingPassNumber;
	private String seatNumber;
	private String zone;
	private int age;
	private boolean veteran;

	public Passenger() {
	}

	public Passenger(String passengerName, String boardingPassNumber, String seatNumber, String zone, int age,
			boolean veteran) {
		this.passengerName = passengerName;
		this.boardingPassNumber = boardingPassNumber;
		this.seatNumber = seatNumber;
		this.zone = zone;
		this.age = age;
		this.veteran = veteran;
	}

	public String getPassengerName() {
		return passengerName;
	}

	public void setPassengerName(String passengerName) {
		this.passengerName = passengerName;
	}

	public String getBoardingPassNumber() {
		return boardingPassNumber;
	}

	public void setBoardingPassNumber(String boardingPassNumber) {
		this.boardingPassNumber = boardingPassNumber;
	}

	public String getSeatNumber() {
		return seatNumber;
	}

	public void setSeatNumber(String seatNumber) {
		this.seatNumber = seatNumber;
	}

	public String getZone() {
		return zone;
	}

	public void setZone(String zone) {
		this.zone = zone;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public boolean isVeteran() {
		return veteran;
	}

	public void setVeteran(boolean veteran) {
		this.veteran = veteran;
	}
}
