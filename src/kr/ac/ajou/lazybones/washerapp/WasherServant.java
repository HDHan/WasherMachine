package kr.ac.ajou.lazybones.washerapp;

import kr.ac.ajou.lazybones.washerapp.Washer.WasherPOA;

/**
 * Washer Servant
 * 
 * @author AJOU
 *
 */
public class WasherServant extends WasherPOA {

	private String washerName;
	ReservationQueueServant reservationQueue = new ReservationQueueServant();

	int count = 0;
	private boolean isOn;

	/*
	 *  Set washer's name
	 */
	public void setWasherName(String washerName) {
		this.washerName = washerName;
	}

	/*
	 *  Get washer's name
	 */
	public String getWasherName() {
		return this.washerName;
	}

	/*
	 *  Turn on the washer
	 * @see kr.ac.ajou.lazybones.washerapp.Washer.WasherOperations#on()
	 */
	@Override
	public boolean on() {
		// Switch on
		if (isOn) { // already on
			System.out.println("It's already on.");
			return false;
		}
		isOn = true;
		System.out.println(washerName + " is on now.");

		// After Time with Duration, remove it.
		while (!reservationQueue.isEmpty() && isOn) {
			try {
				// Wait for (duration * 1000) milliseconds
				Thread.sleep(reservationQueue.getReservationByIndex(0).getDuration() * 1000);
				// Remove reservation
				reservationQueue.dequeue();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		return true;
	}

	/*
	 *  Turn off the washer
	 * @see kr.ac.ajou.lazybones.washerapp.Washer.WasherOperations#off()
	 */
	@Override
	public boolean off() {
		// Switch off
		if (!isOn) { // already off
			System.out.println(washerName + " is already off.");
			return false;
		}
		isOn = false;
		System.out.println(washerName + " is off now.");

		return true;
	}

	/*
	 * Get total waiting time
	 * @see kr.ac.ajou.lazybones.washerapp.Washer.WasherOperations#runningTime()
	 */
	@Override
	public long runningTime() {
		return reservationQueue.getTotalWaitingTime();
	}

}
