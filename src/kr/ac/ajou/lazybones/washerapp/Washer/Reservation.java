package kr.ac.ajou.lazybones.washerapp.Washer;

/**
 * kr/ac/ajou/lazybones/washerapp/Washer/Reservation.java . Generated by the
 * IDL-to-Java compiler (portable), version "3.2" from washer.idl 2015년 6월 10일
 * 수요일 오후 6시 56분 03초 KST
 */

public final class Reservation implements org.omg.CORBA.portable.IDLEntity {
	public String who = null;
	public long duration = 0;

	public Reservation() {
	} // ctor

	public Reservation(String _who, long _duration) {
		who = _who;
		duration = _duration;
	} // ctor

	// These two are necessary for searching with who / for returning duration
	public String getWho() {
		return who;
	}

	public long getDuration() {
		return duration;
	}

} // class Reservation
