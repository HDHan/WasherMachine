package kr.ac.ajou.lazybones.washerapp;

import java.util.LinkedList;
import java.util.Queue;

import kr.ac.ajou.lazybones.washerapp.washer.WasherPOA;

/**
 * Not implemented yet.
 * @author AJOU
 *
 */
public class WasherServant extends WasherPOA{

	private String washerName;
	
	Queue<Reservation> reservationQueue;
	
	int count = 0;
	private boolean isOn;
	
	// Constructor with String washerName
	public WasherServant(String washerName) {
		super();
		this.washerName = washerName;
		
		// Make Queue
		reservationQueue = new LinkedList<Reservation>();
	}

	@Override
	public boolean on() {
		// Switch on
		if(isOn) { // already on
			System.out.println("It's already on.");
			return false;
		}
		isOn = true;
		System.out.println(washerName + " is on now.");
		
		// After Time with Duration, remove it.
		while(!reservationQueue.isEmpty() && isOn) {
			try {
				// Wait with duration * 1000 milliseconds and remove reservation
				Thread.sleep(reservationQueue.peek().getDuration()*1000);
				reservationQueue.remove();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}	
		return true;
	}

	@Override
	public boolean off() {
		// Switch off
		if(!isOn) { // already off
			System.out.println(washerName + " is already off.");
			return false;
		}
		isOn = false;
		System.out.println(washerName + " is off now.");

		return true;
	}

	@Override
	public void reservation(String who, long duration) {		
		// Make Reservation 
		Reservation reservation = new Reservation(who, duration);
		
		// Insert Reservation
		reservationQueue.offer(reservation);
	}

	
}
