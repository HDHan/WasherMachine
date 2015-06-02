package kr.ac.ajou.lazybones.washerapp.Washer;


/**
* kr/ac/ajou/lazybones/washerapp/Washer/ReservationQueuePOA.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from washer.idl
* 2015년 6월 2일 화요일 오후 4시 14분 29초 KST
*/

public abstract class ReservationQueuePOA extends org.omg.PortableServer.Servant
 implements kr.ac.ajou.lazybones.washerapp.Washer.ReservationQueueOperations, org.omg.CORBA.portable.InvokeHandler
{

  // Constructors

  private static java.util.Hashtable _methods = new java.util.Hashtable ();
  static
  {
    _methods.put ("_get_reservations", new java.lang.Integer (0));
    _methods.put ("enqueue", new java.lang.Integer (1));
    _methods.put ("dequeue", new java.lang.Integer (2));
    _methods.put ("isEmpty", new java.lang.Integer (3));
    _methods.put ("isFull", new java.lang.Integer (4));
    _methods.put ("remove", new java.lang.Integer (5));
  }

  public org.omg.CORBA.portable.OutputStream _invoke (String $method,
                                org.omg.CORBA.portable.InputStream in,
                                org.omg.CORBA.portable.ResponseHandler $rh)
  {
    org.omg.CORBA.portable.OutputStream out = null;
    java.lang.Integer __method = (java.lang.Integer)_methods.get ($method);
    if (__method == null)
      throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);

    switch (__method.intValue ())
    {
       case 0:  // Washer/ReservationQueue/_get_reservations
       {
         kr.ac.ajou.lazybones.washerapp.Washer.Reservation $result[] = null;
         $result = this.reservations ();
         out = $rh.createReply();
         kr.ac.ajou.lazybones.washerapp.Washer.ReservationSeqHelper.write (out, $result);
         break;
       }

       case 1:  // Washer/ReservationQueue/enqueue
       {
         kr.ac.ajou.lazybones.washerapp.Washer.Reservation reservation = kr.ac.ajou.lazybones.washerapp.Washer.ReservationHelper.read (in);
         boolean $result = false;
         $result = this.enqueue (reservation);
         out = $rh.createReply();
         out.write_boolean ($result);
         break;
       }

       case 2:  // Washer/ReservationQueue/dequeue
       {
         kr.ac.ajou.lazybones.washerapp.Washer.Reservation $result = null;
         $result = this.dequeue ();
         out = $rh.createReply();
         kr.ac.ajou.lazybones.washerapp.Washer.ReservationHelper.write (out, $result);
         break;
       }

       case 3:  // Washer/ReservationQueue/isEmpty
       {
         boolean $result = false;
         $result = this.isEmpty ();
         out = $rh.createReply();
         out.write_boolean ($result);
         break;
       }

       case 4:  // Washer/ReservationQueue/isFull
       {
         boolean $result = false;
         $result = this.isFull ();
         out = $rh.createReply();
         out.write_boolean ($result);
         break;
       }

       case 5:  // Washer/ReservationQueue/remove
       {
         int index = in.read_long ();
         boolean $result = false;
         $result = this.remove (index);
         out = $rh.createReply();
         out.write_boolean ($result);
         break;
       }

       default:
         throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
    }

    return out;
  } // _invoke

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:Washer/ReservationQueue:1.0"};

  public String[] _all_interfaces (org.omg.PortableServer.POA poa, byte[] objectId)
  {
    return (String[])__ids.clone ();
  }

  public ReservationQueue _this() 
  {
    return ReservationQueueHelper.narrow(
    super._this_object());
  }

  public ReservationQueue _this(org.omg.CORBA.ORB orb) 
  {
    return ReservationQueueHelper.narrow(
    super._this_object(orb));
  }


} // class ReservationQueuePOA
