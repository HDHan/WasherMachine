package kr.ac.ajou.lazybones.washerapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import kr.ac.ajou.lazybones.washerapp.Washer.ReservationQueue;
import kr.ac.ajou.lazybones.washerapp.Washer.ReservationQueueHelper;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.Binding;
import org.omg.CosNaming.BindingHolder;
import org.omg.CosNaming.BindingIteratorHolder;
import org.omg.CosNaming.BindingListHolder;
import org.omg.CosNaming.BindingType;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextHelper;
import org.omg.CosNaming.NamingContextPackage.AlreadyBound;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

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

	private static NamingContextExt namingService;

	// servant: WasherServant which the daemon holds
	private WasherServant washerServant;

	// servant: ReservationQueueServant which the daemon holds
	private ReservationQueueServant queueServant;

	// orb: Object Request Broker object for shutting down
	private ORB orb;

	public WasherDaemon(String[] args, String washerName) {
		isSetup = false;
		this.args = args;
		this.washerName = washerName;
	}

	public WasherServant getWasherServant() {
		return washerServant;
	}

	public ReservationQueueServant getQueueServant() {
		return queueServant;
	}

	/**
	 * Initialize object request broker, register new WasherDaemon reference,
	 * and wait for requests for the reference.
	 * 
	 */
	public void setup() {

		Properties props = new Properties();
		props.put("org.omg.CORBA.ORBInitialPort", "1050");
		props.put("org.omg.CORBA.ORBInitialHost", "210.107.197.213"); 

		// STEP 1: create and initialize the ORB
		orb = ORB.init(args, props);

		POA rootpoa;

		try {
			// STEP 2: get reference to rootpoa & activate the
			// POAManager
			rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
			rootpoa.the_POAManager().activate();

			// STEP 3: create servant object
			washerServant = new WasherServant();
			queueServant = washerServant.reservationQueue;
			washerServant.setWasherName(washerName);

			queueServant.setORB(orb);

			// STEP 4: get an object reference based on the servant
			// implementation.
			org.omg.CORBA.Object ref = rootpoa.servant_to_reference(queueServant);

			ReservationQueue queue = ReservationQueueHelper.narrow(ref);

			// STEP 5: get reference of the root naming context (naming
			// service).
			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
			ncRef = NamingContextExtHelper.narrow(objRef);

			// STEP 6: register the CORBA object reference to the naming
			// service with
			// washerName. Client must search the object using the
			// same name.
			String name = washerName;
			path = ncRef.to_name(name);

			// ncRef.bind(path, queue);

			BindingListHolder bl = new BindingListHolder();
			BindingIteratorHolder blIt = new BindingIteratorHolder();
			boolean isAlreadyRegistered = false;

			// Search washer by name
			ncRef.list(1000, bl, blIt);
			Binding bindings[] = bl.value;
			for (int i = 0; i < bindings.length; i++) {
				int lastIx = bindings[i].binding_name.length - 1;
				if (bindings[i].binding_type == BindingType.nobject
						&& name.equals(bindings[i].binding_name[lastIx].id)) {
					isAlreadyRegistered = true;
				}
			}

			// Do not register washer if the same name is already registered
			if (isAlreadyRegistered) {
				// Deleting washer test
				ncRef.unbind(path);
				System.out.println("unbind");
				unregisterFromServer(name);

				// Print registered washers...
				for (int i = 0; i < bindings.length; i++) {
					int lastIx = bindings[i].binding_name.length - 1;

					// check to see if this is a naming context
					if (bindings[i].binding_type == BindingType.nobject) {
						System.out.println("Object: " + bindings[i].binding_name[lastIx].id);
					}
				}
			} else {
				ncRef.bind(path, queue);
			}


			System.out.println("Daemon is ready and waiting for requests");

			if (!registerToServer(name)) {
				System.out.println("Registering to server failed.");
				return;
			}

			System.out.println("Registered to server successfully.");
			this.isSetup = true;

		} catch (InvalidName | AdapterInactive | ServantNotActive | WrongPolicy
				| org.omg.CosNaming.NamingContextPackage.InvalidName | NotFound | CannotProceed |

		AlreadyBound e)

		{
			e.printStackTrace();
		}

	}

	private boolean registerToServer(String name) {
		CloseableHttpClient httpClient = HttpClients.createDefault();

		HttpGet httpGet = new HttpGet("http://localhost:8080/WasherMan/Washer/Register/" + name);

		System.out.println("Registering the washer to : " + httpGet.getURI());

		try {
			CloseableHttpResponse response = httpClient.execute(httpGet);
			try {
				response.getStatusLine();
				if (response.getStatusLine().getStatusCode() == 200) {
					HttpEntity entity = response.getEntity();
					BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()));

					String result = rd.readLine();
					if (result.equals("OK"))
						return true;
					else
						return false;

				} else
					return false;
			} finally {
				response.close();
			}

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	private boolean unregisterFromServer(String name) {
		CloseableHttpClient httpClient = HttpClients.createDefault();

		HttpGet httpGet = new HttpGet("http://localhost:8080/WasherMan/Washer/Unregister/" + name);

		System.out.println("Unregistering the washer from : " + httpGet.getURI());

		try {
			CloseableHttpResponse response = httpClient.execute(httpGet);
			try {
				response.getStatusLine();
				if (response.getStatusLine().getStatusCode() == 200) {
					HttpEntity entity = response.getEntity();
					BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()));

					String result = rd.readLine();
					if (result.equals("OK"))
						return true;
					else
						return false;

				} else
					return false;
			} finally {
				response.close();
			}

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
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
		this.unregisterFromServer(washerName);
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