package kr.ac.ajou.lazybones.washerapp;

import java.util.ArrayList;

import kr.ac.ajou.lazybones.washerapp.Washer.Reservation;
import kr.ac.ajou.lazybones.washerapp.Washer.ReservationQueuePOA;

/**
 * Not implemented yet.
 * @author AJOU
 *
 */
public class ReservationQueueServant extends ReservationQueuePOA {

	ArrayList<Reservation> reservations;
	
	public ReservationQueueServant(){
		reservations = new ArrayList<>();
	}
	
	@Override
	public Reservation[] reservations() {
		return reservations.toArray(new Reservation[reservations.size()]);
	}


	@Override
	public Reservation dequeue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean remove(int index) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Reservation[] reservationsBy(String who) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean enqueue(String who, long duration) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

}
