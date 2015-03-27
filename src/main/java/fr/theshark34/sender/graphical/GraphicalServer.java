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
package fr.theshark34.sender.graphical;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JOptionPane;

import fr.theshark34.sender.Main;
import fr.theshark34.sender.SenderID;

/**
 * The Graphical Server
 *
 * <p>
 * This is a server waiting for a client sending files to it. It also updates
 * the info label and the progress bar of the info frame.
 * </p>
 *
 * @author TheShark34
 * @version RELEASE-1.0
 */
public class GraphicalServer {

	/**
	 * The server socket
	 */
	private static ServerSocket socket;

	/**
	 * True if the server is alread receiving files
	 */
	private static boolean receiving = false;

	/**
	 * Start the server in a new Thread
	 */
	public static void startServer() {
		// Creating a new Thread
		Thread t = new Thread() {
			@Override
			public void run() {
				try {
					// Starting server
					socket = new ServerSocket(Main.PORT);

					// Listen for a connection
					listen();
				} catch (BindException e) {
					// If it failed to bind the port, printing the exception
					e.printStackTrace();

					// Dispaying an error message
					JOptionPane
							.showMessageDialog(
									null,
									"Unable to launcher the server, a program is using the ports (maybe Sender is already open) : "
											+ e, "Error",
									JOptionPane.ERROR_MESSAGE);

					// And exiting
					System.exit(1);
				} catch (IOException e) {
					// If it failed to start the server
					e.printStackTrace();

					// Displaying an error message
					JOptionPane.showMessageDialog(null,
							"Unable to launch the server : " + e, "Error",
							JOptionPane.ERROR_MESSAGE);

					// And exiting
					System.exit(1);
				}
			}
		};
		t.start();
	}

	/**
	 * Listen for a connection
	 * 
	 * @throws IOException
	 *             If it fails to create the connection
	 */
	private static void listen() throws IOException {
		while (true) {
			// Waiting for a connection
			final Socket client = socket.accept();

			// Sending "busy" to the client if we're already receiving files
			if (receiving) {
				client.getOutputStream().write("busy#".getBytes());
				client.close();
			} else {
				// Else starting receiving in a new Thread
				Thread t = new Thread() {
					@Override
					public void run() {
						// Setting receiving to true
						receiving = true;

						// Changing Sender state
						GraphicalSender.instance
								.setCurrentState(GraphicalSender.RECEIVING);

						// Changing info label
						GraphicalSender.instance.getInfoFrame().setInfo(
								"Receiving from ID "
										+ SenderID.ipToID(socket
												.getInetAddress()
												.getHostAddress()));

						try {
							// Receiving file
							receive(client);

							// Unzip received file
							GraphicalZipper.unzip();
						} catch (IOException e) {
							e.printStackTrace();
							JOptionPane.showMessageDialog(null,
									"Unable to receive the file : " + e,
									"Error", JOptionPane.ERROR_MESSAGE);
						} finally {
							// Setting receiving to false
							receiving = false;

							// Changing Sender state
							GraphicalSender.instance
									.setCurrentState(GraphicalSender.WAITING);

							// Changing info label
							GraphicalSender.instance.getInfoFrame().setInfo(
									"Waiting");

							// Setting bar value to 0
							GraphicalSender.instance.getInfoFrame()
									.getProgressBar().setValue(0);
						}
					}
				};
				t.start();
			}
		}
	}

	/**
	 * Receive a file
	 * 
	 * @param client
	 *            The client (the sender)
	 * @throws IOException
	 */
	private static void receive(Socket client) throws IOException {
		System.out.println("Server : Receiving file");

		// Creating .Sender folder if it doesn't exist
		File folder = new File(System.getProperty("user.home") + "/.Sender/");
		if (!folder.exists())
			folder.mkdirs();

		// Initializing streams
		InputStream input = client.getInputStream();
		OutputStream output = client.getOutputStream();

		// Sending to client that we're ready
		output.write("go#".getBytes());

		// Receiving file size
		String str = "";
		char c;
		while ((c = (char) input.read()) != '#')
			str += c;

		int fileSize = Integer.parseInt(str);
		System.out.println("Server : File size is " + fileSize);

		// Setting bar maximum
		GraphicalSender.instance.getInfoFrame().getProgressBar()
				.setMaximum(fileSize);

		// Creating destination file
		File file = new File(folder, "receivedZipTemp.zip");
		new File(file.getParent()).mkdirs();

		// Receiving file
		OutputStream fileOutput = new FileOutputStream(file);
		byte buf[] = new byte[1024];
		int n;
		int received = 0;
		while ((n = input.read(buf)) != -1) {
			// Write received buffer
			fileOutput.write(buf, 0, n);

			// Setting bar value
			GraphicalSender.instance.getInfoFrame().getProgressBar()
					.setValue(received += n);
		}

		// Closing outputs
		fileOutput.close();
		output.close();
		input.close();
		client.close();

		System.out.println("Server : Done");
	}

}
