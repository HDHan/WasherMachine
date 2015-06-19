/*
 * WasherDaemon
 * 	setup()
 *  searchWasherByName
 *  printWasherList
 *  registerWasherByName
 *  unregisterWasherByName
 *  unregisterWasherByName
 *  unregisterWasherAll
 *  shutdown
 *  run
 *  interrupt
 */


package kr.ac.ajou.lazybones.washerapp;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import kr.ac.ajou.lazybones.washerapp.Washer.ReservationQueue;
import kr.ac.ajou.lazybones.washerapp.Washer.ReservationQueueHelper;
import kr.ac.ajou.lazybones.washerapp.servant.ReservationQueueServant;
import kr.ac.ajou.lazybones.washerapp.servant.WasherServant;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.Binding;
import org.omg.CosNaming.BindingIteratorHolder;
import org.omg.CosNaming.BindingListHolder;
import org.omg.CosNaming.BindingType;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

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
	private NamingContextExt ncRef;
	private NameComponent path[];
	POA rootpoa;

	// servant: WasherServant, ReservationQueueServant which the daemon holds
	private Map<String, WasherServant> washerServants; 
	private Map<String, ReservationQueueServant> queueServants;

	// orb: Object Request Broker object for shutting down
	private ORB orb;

	public WasherDaemon(String[] args) {
		isSetup = false;
		this.args = args;
		
		washerServants = new HashMap<>();
		queueServants = new HashMap<>();
	}

	public WasherServant getWasherServant(String name) {
		return washerServants.get(name);
	}

	public ReservationQueueServant getQueueServant(String name) {
		return queueServants.get(name);
	}

	/**
	 * Initialize object request broker and get reference of the name service
	 * 
	 */
	public boolean setup() {

		Properties props = new Properties();
		props.put("org.omg.CORBA.ORBInitialPort", "1050");
		props.put("org.omg.CORBA.ORBInitialHost", "210.107.197.213");

		// STEP 1: Create and initialize the ORB
		orb = ORB.init(args, props);

		try {
			// STEP 2: Get reference to rootpoa & activate the POAManager
			rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
			rootpoa.the_POAManager().activate();

			// STEP 5: Get reference of the root naming context (naming
			// service).
			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
			ncRef = NamingContextExtHelper.narrow(objRef);

			System.out.println("CORBA orbd server is connected successfully.");
			this.isSetup = true;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return this.isSetup;
	}

	/**
	 * Search washer by name from orb Return true if wahserName exists
	 * 
	 */
	public boolean searchWasherByName(String washerName) {
		BindingListHolder bl = new BindingListHolder();
		BindingIteratorHolder blIt = new BindingIteratorHolder();
		ncRef.list(1000, bl, blIt);
		Binding bindings[] = bl.value;

		for (int i = 0; i < bindings.length; i++) {
			int lastIx = bindings[i].binding_name.length - 1;
			if (bindings[i].binding_type == BindingType.nobject
					&& washerName.equals(bindings[i].binding_name[lastIx].id)) {
				
				return true;
			}
		}
		return false;
	}

	/**
	 * Print washer list from object request broker daemon.
	 * 
	 */
	public boolean printWasherList() {
		BindingListHolder bl = new BindingListHolder();
		BindingIteratorHolder blIt = new BindingIteratorHolder();
		ncRef.list(1000, bl, blIt);
		Binding bindings[] = bl.value;

		// List Size == 0
		if (bindings.length == 0) {
			System.out.println("No Washers are registered yet.");
			return true;
		}
		
		// Print registered washers.
		System.out.println("--------------------------------");
		System.out.println("Registered Washers from ORBD:");
		for (int i = 0; i < bindings.length; i++) {
			int lastIx = bindings[i].binding_name.length - 1;
			// check to see if this is a naming context
			if (bindings[i].binding_type == BindingType.nobject) {
				System.out.println("	" + bindings[i].binding_name[lastIx].id);
			}
		}
		System.out.println("--------------------------------");
		return true;
	}

	/**
	 * Register washer by name to orbd name service
	 * 
	 */
	public boolean registerWasherByName(String washerName) {
		try {
			// Create servant object
			WasherServant washerServant = new WasherServant(washerName);
			ReservationQueueServant queueServant = washerServant.getReservationQueueServant();
			
			queueServant.setORB(orb);

			// Get an object reference based on the servant
			// implementation.
			org.omg.CORBA.Object ref = rootpoa.servant_to_reference(queueServant);

			ReservationQueue queue = ReservationQueueHelper.narrow(ref);

			// Register the CORBA object reference to the naming service
			// with washerName. Client must search the object using the same
			// name.
			path = ncRef.to_name(washerName);
			ncRef.bind(path, queue);
			
			washerServants.put(washerName, washerServant);
			queueServants.put(washerName, queueServant);

			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/*
	 * Unregister washer By Name Return true if unregistering suceeded
	 */
	public boolean unregisterWasherByName(String washerName) {
		BindingListHolder bl = new BindingListHolder();
		BindingIteratorHolder blIt = new BindingIteratorHolder();
		ncRef.list(1000, bl, blIt);
		Binding bindings[] = bl.value;

		try {
			for (int i = 0; i < bindings.length; i++) {
				int lastIx = bindings[i].binding_name.length - 1;
				if (bindings[i].binding_type == BindingType.nobject
						&& washerName.equals(bindings[i].binding_name[lastIx].id)) {
					path = ncRef.to_name(washerName);
					ncRef.unbind(path);
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/*
	 * Unregister all washers in name service
	 */
	public boolean unregisterWasherAll() {
		boolean result = false;

		BindingListHolder bl = new BindingListHolder();
		BindingIteratorHolder blIt = new BindingIteratorHolder();
		ncRef.list(1000, bl, blIt);
		Binding bindings[] = bl.value;

		try {
			for (int i = 0; i < bindings.length; i++) {
				int lastIx = bindings[i].binding_name.length - 1;
				if (bindings[i].binding_type == BindingType.nobject) {
					path = ncRef.to_name(bindings[i].binding_name[lastIx].id);
					ncRef.unbind(path);
					// unregisterFromServer(bindings[i].binding_name[lastIx].id);
				}
			}
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/*
	 * Deprecated (since WasherMan does not need manual registration) 
	 * 
	 * private boolean registerToServer(String washerName) { CloseableHttpClient
	 * httpClient = HttpClients.createDefault();
	 * 
	 * HttpGet httpGet = new
	 * HttpGet("http://210.107.197.213:8080/WasherMan/Washer/Register/" +
	 * washerName);
	 * 
	 * System.out.println("Registering the washer to : " + httpGet.getURI());
	 * 
	 * try { CloseableHttpResponse response = httpClient.execute(httpGet); try {
	 * response.getStatusLine(); if (response.getStatusLine().getStatusCode() ==
	 * 200) { HttpEntity entity = response.getEntity(); BufferedReader rd = new
	 * BufferedReader(new InputStreamReader(entity.getContent()));
	 * 
	 * String result = rd.readLine(); if (result.equals("OK")) return true; else
	 * return false;
	 * 
	 * } else return false; } finally { response.close(); }
	 * 
	 * } catch (ClientProtocolException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); return false; } catch (IOException e) { // TODO
	 * Auto-generated catch block e.printStackTrace(); return false; } }
	 * 
	 * private boolean unregisterFromServer(String name) { CloseableHttpClient
	 * httpClient = HttpClients.createDefault();
	 * 
	 * HttpGet httpGet = new
	 * HttpGet("http://localhost:8080/WasherMan/Washer/Unregister/" + name);
	 * 
	 * System.out.println("Unregistering the washer from : " +
	 * httpGet.getURI());
	 * 
	 * try { CloseableHttpResponse response = httpClient.execute(httpGet); try {
	 * response.getStatusLine(); if (response.getStatusLine().getStatusCode() ==
	 * 200) { HttpEntity entity = response.getEntity(); BufferedReader rd = new
	 * BufferedReader(new InputStreamReader(entity.getContent()));
	 * 
	 * String result = rd.readLine(); if (result.equals("OK")) return true; else
	 * return false;
	 * 
	 * } else return false; } finally { response.close(); }
	 * 
	 * } catch (ClientProtocolException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); return false; } catch (IOException e) { // TODO
	 * Auto-generated catch block e.printStackTrace(); return false; } }
	 */

	/**
	 * Shutdown all running machines.
	 */
	public void shutdown() {
		// Unregister All Washers from orbd
		unregisterWasherAll();
		// this.unregisterFromServer(washerName);
		orb.shutdown(false);
	}

	/**
	 * Inherited method from thread class.	
	 */
	@Override
	public void run() {
		// STEP 7: wait for requests from clients
		if (isSetup)
			orb.run();
		else
			System.out.println("Not set up yet.");
	}

	/**
	 * Inherited method from thread class.
	 */
	@Override
	public void interrupt() {
		System.out.println("Shutting down the daemon");
		this.shutdown();
		super.interrupt();
	}

}