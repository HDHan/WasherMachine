package kr.ac.ajou.lazybones.washerapp;

public class Reservation {
	String who;
	long duration;
	
	public Reservation() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Reservation(String who, long duration) {
		super();
		this.who = who;
		this.duration = duration;
	}
	
	public String getWho() {
		return who;
	}
	public void setWho(String who) {
		this.who = who;
	}
	public long getDuration() {
		return duration;
	}
	public void setDuration(long duration) {
		this.duration = duration;
	}
	
	
}
