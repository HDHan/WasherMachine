package kr.ac.ajou.lazybones.washerapp;

import kr.ac.ajou.lazybones.washerapp.Washer.ReservationQueue;
import kr.ac.ajou.lazybones.washerapp.Washer.WasherPOA;

/**
 * Not implemented yet.
 * @author AJOU
 *
 */
public class WasherServant extends WasherPOA{

	private String name;
	ReservationQueue reservationQueue;
	
	int count = 0;
	private boolean isOn;
	
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getName(){
		return this.name;
	}
	
	public void setReservationQueue(ReservationQueue reservationQueue) {
		this.reservationQueue = reservationQueue;
	}

	@Override
	public ReservationQueue reservationQueue() {
		// TODO Auto-generated method stub
		return reservationQueue;
	}

	@Override
	public boolean on() {
		// TODO Auto-generated method stub
		isOn = true;
		System.out.println(name + ", "+ count++);
		
		return true;
	}

	@Override
	public boolean off() {
		// TODO Auto-generated method stub
		isOn = false;
		return true;
	}

}
