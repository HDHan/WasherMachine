package kr.ac.ajou.lazybones.washerapp;

import kr.ac.ajou.lazybones.washerapp.Washer.ReservationQueue;
import kr.ac.ajou.lazybones.washerapp.Washer.WasherPOA;

public class WasherServant extends WasherPOA{

	ReservationQueue reservationQueue;
	
	
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
		return false;
	}

	@Override
	public boolean off() {
		// TODO Auto-generated method stub
		return false;
	}

}
