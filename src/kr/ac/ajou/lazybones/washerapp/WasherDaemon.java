package kr.ac.ajou.lazybones.washerapp;

import java.util.Properties;

import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.AlreadyBound;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

import kr.ac.ajou.lazybones.washerapp.washer.Washer;
import kr.ac.ajou.lazybones.washerapp.washer.WasherHelper;

/**
 * Roughly implemented daemon. It registers reference of WasherServant to ORB
 * daemon on arbitrary server and starts to wait for requests.
 * 
 * @author AJOU
 *
 */
public class WasherDaemon extends Thread {

	// args: arguments from java application arguments
	// washerName: the temporal name of Washer
	private boolean isSetup;
	private String[] args;
	private String washerName;
	private NamingContextExt ncRef;
	private NameComponent path[];

	// servant: WasherServant which the daemon holds
	private WasherServant servant;

	// orb: Object Request Broker object for shutting down
	private ORB orb;

	public WasherDaemon(String[] args, String washerName) {
		isSetup = false;
		this.args = args;
		this.washerName = washerName;
	}

	public WasherServant getServant() {
		return servant;
	}

	/**
	 * Initialize object request broker, register new WasherDaemon reference,
	 * and wait for requests for the reference.
	 * 
	 */
	public void setup() {

		Properties props = new Properties();
		props.put("org.omg.CORBA.ORBInitialPort", "1050");
		//props.put("org.omg.CORBA.ORBInitialHost", "210.107.197.213");
		props.put("org.omg.CORBA.ORBInitialHost", "localhost");

		// STEP 1: create and initialize the ORB
		orb = ORB.init(args, props);

		POA rootpoa;

		try {
			// STEP 2: get reference to rootpoa & activate the
			// POAManager
			rootpoa = POAHelper.narrow(orb
					.resolve_initial_references("RootPOA"));
			rootpoa.the_POAManager().activate();

			// STEP 3: create servant object and give it the ORB
			// reference (for shutdown())
			servant = new WasherServant(washerName);

			// servant.setORB(orb);

			// STEP 4: get an object reference based on the servant
			// implementation.
			// The object reference must be narrowed to the correct
			// type, in this case Hello.
			org.omg.CORBA.Object ref = rootpoa.servant_to_reference(servant);

			Washer washer = WasherHelper.narrow(ref);

			// STEP 5: get reference of the root naming context (naming
			// service).
			org.omg.CORBA.Object objRef = orb
					.resolve_initial_references("NameService");
			ncRef = NamingContextExtHelper.narrow(objRef);

			// STEP 6: register the CORBA object reference to the naming
			// service with
			// washerName. Client must search the object using the
			// same name.
			String name = washerName;
			path = ncRef.to_name(name);
			ncRef.bind(path, washer);

			// ncRef.unbind(path);

			System.out.println("Daemon is ready and waiting for requests");

			this.isSetup = true;

		} catch (InvalidName | AdapterInactive | ServantNotActive | WrongPolicy
				| org.omg.CosNaming.NamingContextPackage.InvalidName | NotFound
				| CannotProceed | AlreadyBound e) {
			e.printStackTrace();
		}

	}

	public void shutdown() {
		try {
			ncRef.unbind(path);
		} catch (NotFound e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CannotProceed e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (org.omg.CosNaming.NamingContextPackage.InvalidName e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		orb.shutdown(false);
	}

	@Override
	public void run() {
		// STEP 7: wait for requests from clients
		if (isSetup)
			orb.run();
		else
			System.out.println("Not set up yet.");
	}

	@Override
	public void interrupt() {
		System.out.println("Shutting down the daemon");
		this.shutdown();
		super.interrupt();
	}

	public boolean isSetup() {
		return this.isSetup;
	}

}