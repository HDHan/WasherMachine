package kr.ac.ajou.lazybones.washerapp.servant;

import java.util.Timer;
import java.util.TimerTask;

import kr.ac.ajou.lazybones.washerapp.Washer.Reservation;
import kr.ac.ajou.lazybones.washerapp.Washer.WasherPOA;

/**
 * Washer Servant
 * 
 * @author AJOU
 *
 */
public class WasherServant extends WasherPOA {
	
	private class WasherTimerTask extends TimerTask{
		
		WasherServant washer;
		
		public WasherTimerTask(WasherServant washer){
			this.washer = washer;
		}
		
		@Override
		public void run() {
			//It simply stops the given washer.
			System.out.println("DONE!");
			washer.off();
		}
		
	};
	
	private Timer timer = new Timer();
	private String washerName;
	private ReservationQueueServant reservationQueueServant = new ReservationQueueServant();

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
	 * Get ReservaitonQueueServant
	 */
	public ReservationQueueServant getReservationQueueServant() {
		return reservationQueueServant;
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
		System.out.println(washerName + " is on now.");
		
		if(!reservationQueueServant.isEmpty()){
			Reservation reservation = reservationQueueServant.dequeue();
			timer.schedule(new WasherTimerTask(this), reservation.getDuration()*60*1000);		
			isOn = true;			
		}
		else{
			System.out.println("No reservation exists!");
			isOn = false;
		}
		
//		// After Time with Duration, remove it.
//		while (!reservationQueue.isEmpty() && isOn) {
//			try {
//				// Wait for (duration * 1000) milliseconds
//				Thread.sleep(reservationQueue.getReservationByIndex(0).getDuration() * 1000);
//				// Remove reservation
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}

		return isOn;
	}

	/*
	 *  Turn off the washer
	 * @see kr.ac.ajou.lazybones.washerapp.Washer.WasherOperations#off()
	 */
	@Override
	public boolean off() {
		
		//Purge all scheduled tasks in timer(To avoid duplicates)
		timer.purge();
		
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
