package kr.ac.ajou.lazybones.washerapp;

import java.util.Scanner;

public class WasherMachineUI {

	// scanner is used for console input. DO NOT use this on thread.
	private static final Scanner scanner = new Scanner(System.in);

	// Initialize and run UI and daemon
	public static void main(String[] args) {
		System.out.println("Please set the name of this machine.");
		String name = scanner.next();

		WasherMachineUI ui = new WasherMachineUI();
		WasherDaemon daemon1 = new WasherDaemon(args, name);
		daemon1.setup();

		if (daemon1.isSetup()) {
			daemon1.start();
			ui.waitForTurningOnOff(daemon1);
		}
	}

	/**
	 * Loop function for turning on/off doing laundry.
	 * 
	 * @param daemon
	 */
	public void waitForTurningOnOff(WasherDaemon daemon) {
		Scanner scanner = new Scanner(System.in); 
		
		boolean isRunning = true;
		// Use while loop to get instructions (on/off/exit)
		while (isRunning) {
			System.out.println("Input 'on' or 'off'. Input 'exit' if you want to exit.");
			System.out.println("You can input 'set' for setting new machine.");

			String input = "";
			if(scanner.hasNext()) {
				 input = scanner.next();
			}
			switch (input.toLowerCase()) {
			case "on":
				if (daemon.getWasherServant().on())
					System.out.println("Washer is turned on.");
				else
					System.out.println("Washer is not ready to turn on yet.");
				break;
			case "off":
				if (daemon.getWasherServant().off())
					System.out.println("Washer is turned off.");
				else
					System.out.println("Washer is not ready to turn off yet.");
				break;
			case "set":
				System.out.println("Please set the name of this machine.");
				String name = scanner.next();
				daemon.setWasherName(name);
				daemon.setup();
				waitForTurningOnOff(daemon);
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
