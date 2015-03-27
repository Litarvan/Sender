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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import javax.swing.JOptionPane;

import fr.theshark34.sender.Main;
import fr.theshark34.sender.SenderID;

/**
 * The Graphical Client
 * 
 * <p>
 * So this is the sender with graphical support, it contains a single method
 * that connects to a server and sends file to him, it also updates the Sender
 * state to update the animations, the info label and the progress bar of the
 * info frame.
 * </p>
 *
 * @author TheShark34
 * @version RELEASE-1.0
 */
public class GraphicalClient {

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
		// Setting state to SENDING
		GraphicalSender.instance.setCurrentState(GraphicalSender.SENDING);

		// Creating a new thread;
		Thread sendThread = new Thread() {
			@Override
			public void run() {
				try {
					// Changing info label
					GraphicalSender.instance.getInfoFrame().setInfo(
							"Connecting to ID " + ID);

					// Setting bar value to 0
					GraphicalSender.instance.getInfoFrame().getProgressBar()
							.setValue(0);

					// Connecting to the server
					socket = new Socket(SenderID.idToIP(ID), Main.PORT);

					// Receiving server response
					InputStream input = socket.getInputStream();
					String str = "";
					char c;
					while ((c = (char) input.read()) != '#')
						str += c;

					// Checking server response
					if (str.equals("busy")) {
						// Changing info label
						GraphicalSender.instance.getInfoFrame().setInfo(
								"Error !");

						// Displaying error message
						JOptionPane.showMessageDialog(GraphicalSender.instance,
								"ID " + ID + " is already receiving files",
								"Error", JOptionPane.ERROR_MESSAGE);

						// Changing info label
						GraphicalSender.instance.getInfoFrame().setInfo(
								"Waiting");
					} else if (!str.equals("go")) {
						// Changing info label
						GraphicalSender.instance.getInfoFrame().setInfo(
								"Error !");

						// Displaying error message
						JOptionPane.showMessageDialog(GraphicalSender.instance,
								"Can't send file to " + ID + " he said : "
										+ str, "Error",
								JOptionPane.ERROR_MESSAGE);

						// Changing info label
						GraphicalSender.instance.getInfoFrame().setInfo(
								"Waiting");
					}
					// Creating the output stream
					OutputStream output = socket.getOutputStream();
					output.write((file.length() + "#").getBytes());

					// Setting bar maximum
					GraphicalSender.instance.getInfoFrame().getProgressBar()
							.setMaximum((int) file.length());

					// Changing info label
					GraphicalSender.instance.getInfoFrame().setInfo(
							"Sending to ID " + ID);

					// Sending file
					InputStream finput = new FileInputStream(file);
					byte buf[] = new byte[1024];
					int n;
					int send = 0;
					while ((n = finput.read(buf)) != -1) {
						// Sending buffer
						output.write(buf, 0, n);

						// Setting bar value
						GraphicalSender.instance.getInfoFrame()
								.getProgressBar().setValue((send += n));
					}

					// Closing all
					finput.close();
					output.close();
					input.close();
					socket.close();
				} catch (IOException e) {
					JOptionPane.showMessageDialog(GraphicalSender.instance,
							"Can't connect to ID " + ID, "Error",
							JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
				} finally {
					// Setting state to WAITING
					GraphicalSender.instance
							.setCurrentState(GraphicalSender.WAITING);

					// Changing info label
					GraphicalSender.instance.getInfoFrame().setInfo("Waiting");

					// Setting bar value to 0
					GraphicalSender.instance.getInfoFrame().getProgressBar()
							.setValue(0);
				}
			}
		};
		sendThread.start();
	}

}
