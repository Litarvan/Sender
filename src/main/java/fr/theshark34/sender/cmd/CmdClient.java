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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import fr.theshark34.sender.Main;
import fr.theshark34.sender.SenderID;

/**
 * The Command-Line Client
 * 
 * <p>
 * So this is the receiver that contains a single method to send files to
 * another computer.
 * </p>
 *
 * @author TheShark34
 * @version RELEASE-1.0
 */
public class CmdClient {

	/**
	 * The client socket
	 */
	private Socket socket;

	/**
	 * Send a file to a server
	 * 
	 * @param file
	 *            The file to send
	 * @param ID
	 *            The receiver's ID
	 * @throws IOException
	 *             If it fails to connect to the server
	 */
	public void sendFile(final File file, final String ID) {
		// Creating a new thread;
		Thread sendThread = new Thread() {
			@Override
			public void run() {
				try {
					// Printing a message
					System.out.println("Connecting to computer with ID " + ID);

					// Connecting to the server
					socket = new Socket(SenderID.idToIP(ID), Main.PORT);

					// Printing a message
					System.out.println("Waiting for his response...");

					// Receiving server response
					InputStream input = socket.getInputStream();
					String str = "";
					char c;
					while ((c = (char) input.read()) != '#')
						str += c;

					// Checking server response
					if (str.equals("busy")) {
						// Printing a message
						System.out
								.println("ERROR: The server is already receiving files from another computer !");
					} else if (!str.equals("go")) {
						// Printing a message
						System.out
								.println("ERROR: Server returned unknown response : "
										+ str);
					}

					// Printing a message
					System.out.println("Initializing...");

					// Creating the output stream
					OutputStream output = socket.getOutputStream();
					output.write((file.length() + "#").getBytes());

					// Sending file
					InputStream finput = new FileInputStream(file);
					byte buf[] = new byte[1024];
					int n;
					int send = 0;
					while ((n = finput.read(buf)) != -1) {
						// Sending buffer
						output.write(buf, 0, n);

						// Getting the percentage
						double percentage = (double) (send += n)
								/ (double) file.length() * 100;

						// Printing it
						System.out.print("Sending..... "
								+ ((int) percentage < 10 ? " " : "")
								+ (int) percentage + "%\r");
					}

					// Printing a message
					System.out.println("Terminated !");

					// Closing all
					finput.close();
					output.close();
					input.close();
					socket.close();
				} catch (IOException e) {
					// If it failed, printing a message
					System.out.println("ERROR: Can't send the file !");

					// Printing the error
					e.printStackTrace();

					// And exitting
					System.exit(1);
				}
			}
		};

		// Starting the thread
		sendThread.start();
	}

}
