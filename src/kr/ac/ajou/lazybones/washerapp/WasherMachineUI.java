package kr.ac.ajou.lazybones.washerapp;

import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;

import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;


public class WasherMachineUI {
	
	//scanner is used for console input. DO NOT use this on thread.
	private static final Scanner scanner = new Scanner(System.in);
	
	//Initialize and run UI and daemon
	public static void main(String[] args) {
		
		// Set Machine Name
		System.out.println("Please set the name of this machine.");
		String machineName = scanner.next();
		
		// Make UI & Daemon
		WasherMachineUI ui = new WasherMachineUI();		
		WasherDaemon daemon1 = new WasherDaemon(args, machineName);
		
		// 
		daemon1.setup();
		
		if(daemon1.isSetup()){
			daemon1.start();
			ui.waitForTurningOnOff(daemon1);
		}
		
	}

	/**
	 * Loop function for turning on/off doing laundry.
	 * @param daemon
	 */
	public void waitForTurningOnOff(WasherDaemon daemon) {
		
		boolean isRunning = true;
		// Use while loop to get instructions (on/off/exit)
		while (isRunning) {
			System.out
					.println("Input 'on' or 'off'. Input 'exit' if you want to exit.");
			String input = scanner.next();
			switch (input.toLowerCase()) {
			case "on":
				if(daemon.getServant().on())
					System.out.println("Washer is turned on.");
				else
					System.out.println("Washer is not ready to turn on yet.");
				break;
			case "off":
				if(daemon.getServant().off())
					System.out.println("Washer is turned off.");
				else 
					System.out.println("Washer is not ready to turn off yet.");
				break;
			case "exit":
				isRunning = false;
				daemon.interrupt();
				break;
			default:
				System.out.println("Wrong input. Please try again.");
				break;

			}
		}
		System.out.println("Bye!");
		scanner.close();
	}

}
