package kr.ac.ajou.lazybones.washerapp;

import java.util.Scanner;

/**
 * Main CLI interface for WasherMachine application. 
 * @author AJOU
 *
 */
public class WasherMachineUI {
	public static void main(String[] args) {

		boolean isRunning = true;
		Scanner scanner = new Scanner(System.in);
		String input = "";

		String washerName = "";

		// Initialize Daemon
		WasherDaemon daemon = new WasherDaemon(args);

		
		if (daemon.setup()) {
			daemon.start();
		} else{
			System.out.println("Failed to initialize daemon.");
			scanner.close();
			return;
		}

		// Use while loop to get instructions (on/off/exit)
		while (isRunning) {
			// First view
			System.out.println("=======================");
			System.out.println("        MENU         ");
			System.out.println("1. View Washers");
			System.out.println("2. Register New Washer");
			System.out.println("3. Unregister Washer");
			System.out.println("4. On/off Washer");
			System.out.println("5. Exit");
			System.out.println("=======================");

			if (scanner.hasNext()) {
				input = scanner.next();
			}

			switch (input.toLowerCase()) {
			case "1": // View Washers
				daemon.printWasherList();
				break;
			case "2": // Register New Washer
				System.out.println("Set new washer name please");
				// User input
				if (scanner.hasNext()) {
					washerName = scanner.next();
					// Special keyword "ALL" is reserved for remove all washers.
					if (washerName.equals("ALL")) {
						System.out.println("You can't use 'ALL' keywords. Try other names.");
						break;
					}
					// Register if same name doesn't exist
					if (daemon.searchWasherByName(washerName)) {
						System.out.println("Already exists! Try new name.");
						break;
					}
					daemon.registerWasherByName(washerName);
				}
				break;
			case "3": // Unregister Washer
				System.out.println("Please set washer name that you want to delete");
				if (scanner.hasNext()) {
					washerName = scanner.next();
					// Special keyword "ALL" is reserved for remove all washers.
					if (washerName.equals("ALL")) {
						daemon.unregisterWasherAll();
						break;
					}
					// Register if same name exists
					if (!daemon.searchWasherByName(washerName)) {
						System.out.println("Doesn't exist! Check washer name.");
						break;
					}
					daemon.unregisterWasherByName(washerName);
				}
				break;
			case "4": // On/Off Washer
				System.out.println("Please input washer name");
				if (scanner.hasNext()) {
					washerName = scanner.next();
					//Check already Exists
					if (!daemon.searchWasherByName(washerName)) {
						System.out.println("Doesn't exist! Check washer name.");
						break;
					}
					System.out.println("Please input 'on' or 'off'");
					if (scanner.hasNext()) {
						input = scanner.next();
						switch (input.toLowerCase()) {
						case "on":
							if (daemon.getWasherServant(washerName).on()) {
								System.out.println("Washer is turned on.");
								break;
							}
							System.out.println("Washer is not ready to turn on yet.");
							break;
						case "off":
							if (daemon.getWasherServant(washerName).off()) {
								System.out.println("Washer is turned off.");
								break;
							}	
							System.out.println("Washer is not ready to turn off yet.");
							break;
						default:
							System.out.println("Wrong input. Please try again.");
						}
					}
				}
				break;
			case "5": // Exit
				isRunning = false;
				daemon.interrupt();
				
				break;
			default:
				System.out.println("Wrong input. Please try again.");
				break;

			/*
			 * case "on": if (daemon.getWasherServant().on())
			 * System.out.println("Washer is turned on."); else
			 * System.out.println("Washer is not ready to turn on yet."); break;
			 * case "off": if (daemon.getWasherServant().off())
			 * System.out.println("Washer is turned off."); else
			 * System.out.println("Washer is not ready to turn off yet.");
			 * break; case "set": System.out.println(
			 * "Please set the name of this machine."); String name =
			 * scanner.next(); daemon.setWasherName(name); daemon.setup();
			 * waitForTurningOnOff(daemon); break; case "exit": isRunning =
			 * false; daemon.interrupt(); break; default: System.out.println(
			 * "Wrong input. Please try again."); break;
			 */
			}
		}
		System.out.println("Bye!");
		scanner.close();
	}
}