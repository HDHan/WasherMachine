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
	ReservationQueueServant reservationQueue = new ReservationQueueServant();
	
	int count = 0;
	private boolean isOn;
	
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getName(){
		return this.name;
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

	@Override
	public long runningTime() {
		// TODO Auto-generated method stub
		return 0;
	}

}
