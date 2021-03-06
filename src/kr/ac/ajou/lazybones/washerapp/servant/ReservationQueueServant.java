package kr.ac.ajou.lazybones.washerapp.servant;

import java.util.ArrayList;

import org.omg.CORBA.ORB;

import kr.ac.ajou.lazybones.washerapp.Washer.Reservation;
import kr.ac.ajou.lazybones.washerapp.Washer.ReservationQueuePOA;

/**
 * Reservation Queue
 * 
 * @author AJOU
 *
 */
public class ReservationQueueServant extends ReservationQueuePOA {

    private ORB orb;

    public void setORB(ORB orb_val) {
        orb = orb_val;
    }
	
	ArrayList<Reservation> reservations;

	public ReservationQueueServant() {
		reservations = new ArrayList<>();
	}

	// Return all reservations
	@Override
	public Reservation[] reservations() {
		return reservations.toArray(new Reservation[reservations.size()]);
	}

	// Return reservations with user name (who)
	@Override
	public Reservation[] reservationsBy(String who) {
		ArrayList<Reservation> myReservations = new ArrayList<>();

		// find all reservation of 'who'
		for (Reservation reservation : reservations) {
			if (reservation.getWho() != null && reservation.getWho().contains(who)) {
				myReservations.add(reservation);
			}
		}

		return myReservations.toArray(new Reservation[myReservations.size()]);
	}
	
	@Override
	public Reservation[] reservationWaitingTimes() {
		ArrayList<Reservation> myReservations = new ArrayList<>();
		Long accumulatedTime = 0l;

		// find all reservation of 'who'
		for (Reservation reservation : reservations) {
			reservation.duration += accumulatedTime;
			myReservations.add(reservation);

			accumulatedTime += reservation.duration;
		}

		return myReservations.toArray(new Reservation[myReservations.size()]);
	}

	@Override
	public Reservation[] reservationWaitingTimesBy(String who) {
		ArrayList<Reservation> myReservations = new ArrayList<>();
		Long accumulatedTime = 0l;

		// find all reservation of 'who'
		for (Reservation reservation : reservations) {
			reservation.duration += accumulatedTime;
			if (reservation.getWho() != null && reservation.getWho().contains(who)) 
				myReservations.add(reservation);
			
			accumulatedTime += reservation.duration;
		}

		return myReservations.toArray(new Reservation[myReservations.size()]);
	}

	// Check if it is empty
	@Override
	public boolean isEmpty() {
		if (reservations.size() == 0) {
			return true;
		}
		return false;
	}

	// Remove with index
	@Override
	public boolean remove(int index) {
		reservations.remove(index);
		return true;
	}

	// Insert reservation
	@Override
	public boolean enqueue(String who, long duration) {
		reservations.add(new Reservation(who, duration));
		return false;
	}

	// Get total waiting time
	public long getTotalWaitingTime() {
		long totalWaitingTime = 0;
		for (Reservation reservation : reservations) {
			totalWaitingTime += reservation.getDuration();
		}
		return totalWaitingTime;
	}

	// Get total waiting time
	public long getTotalWaitingTime(int index) {
		long totalWaitingTime = 0;
		for (int i = 0; i < index; ++i) {
			totalWaitingTime += getReservationByIndex(index).getDuration();
		}
		return totalWaitingTime;
	}

	// Remove first element
	@Override
	public Reservation dequeue() {
		return reservations.remove(0);
	}

	// Return element by index
	public Reservation getReservationByIndex(int index) {
		return reservations.get(index);
	}

	@Override
	public int size() {
		return reservations.size();
	}

}
