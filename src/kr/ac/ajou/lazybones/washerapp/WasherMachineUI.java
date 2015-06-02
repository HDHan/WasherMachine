package kr.ac.ajou.lazybones.washerapp;

import java.io.IOException;
import java.util.Scanner;



public class WasherMachineUI {
	
	
	
	public static void main(String[] args) {

		WasherMachineUI ui = new WasherMachineUI();	
		
		ui.waitForTurningOnOff();
		
	}
	
	public void waitForTurningOnOff(){
		Scanner scanner = new Scanner(System.in);
		
		boolean isRunning = true;
		while(isRunning){
			System.out.println("Input 'on' or 'off'. Input 'exit' if you want to exit.");
			String input = scanner.next();
			switch(input.toLowerCase()){
			case "on":
				
				break;
			case "off":
				break;
			case "exit":
				isRunning=false;
				break;
			default:
				System.out.println("Wrong input. Please try again.");
				break;
					
			}
		}
		System.out.println("Bye!");
	}

}
