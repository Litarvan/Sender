/*
 * Copyright 2015 TheShark34
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package fr.theshark34.sender.cmd;

import java.io.File;
import java.util.Arrays;

import java.util.Scanner;

import fr.theshark34.sender.SenderID;

/**
 * The Command-Line Sender
 *
 * <p>
 * So this is the main command-line class. It reads the given arguments then
 * starts the server, send files, display version, or the help message. Pretty
 * simple.
 * </p>
 *
 * @author TheShark34
 * @version RELEASE-1.0
 */
public class CmdSender {

	/**
	 * The welcome message displayed at the start
	 */
	public static final String WELCOME_MESSAGE = "\nSender CMD Version 1.0-RELEASE";

	/**
	 * Starts the command-line Sender with the given argument
	 * 
	 * @param args
	 *            The given arguments
	 */
	public static void start(String[] args) {
		// Displaying the welcome message
		System.out.println(WELCOME_MESSAGE);

		// If the first argument is -s or --send
		if (args[0].equals("-s") || args[0].equals("--send")) {
			// Checking if the user gave at least a second argument
			if (args.length == 1) {
				// If not displaying the help
				displayHelp();

				// And aborting
				return;
			}

			// Else starting the client !
			else
				startClient(args);
		}

		// Else if the first argument is -r or --server
		else if (args[0].equals("-r") || args[0].equals("--server"))
			startServer();

		// Else if the first argument is an unknown argument
		else
			// Displaying the help message
			displayHelp();
	}

	/**
	 * Starts the command-line server
	 */
	private static void startServer() {
		// Displaying a beautiful line return
		System.out.println();

		// Displaying the Computer ID
		System.out.println("Your ID : " + SenderID.ID);

		// Displaying how to stop the server
		System.out.println("Type 'stop' to stop the server");

		// Displaying a message
		System.out.println("Starting the server....");

		// Starting the server
		CmdServer.startServer();
	}

	/**
	 * Starts the command-line client
	 */
	private static void startClient(String[] args) {
		// Creating a scanner to get the destination computer ID
		Scanner sc = new Scanner(System.in);

		// Printing a message
		System.out.print("\nDestination Computer ID : ");

		// Getting the ID
		int id = sc.nextInt();

		// Closing the scanner
		sc.close();
		
		// Printing a message
		System.out.println("\nPreparing files...");
		
		// Getting the given files but ignoring the '-s'/'--send' argument
		String[] filesStr = Arrays.copyOfRange(args, 1, args.length);

		// Creating a files array with the same length
		File[] files = new File[filesStr.length];

		// Creating files for all these strings
		for (int i = 0; i < filesStr.length; i++)
			files[i] = new File(filesStr[i]);

		// Starting the client
		CmdZipper.indexAndZip(files, id);
	}

	/**
	 * Displays the help message
	 */
	private static void displayHelp() {
		// Just displaying the help ^^
		System.out.println("Usage: sender <option> [files]\n");
		System.out.println(" -h, --help			Displays this message");
		System.out
				.println(" -s, --send <ID> <Files>	Send file(s) to the computer with the given ID");
		System.out
				.println(" -r, --server			Starts the server to receive files");
	}

}
