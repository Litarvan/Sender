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
package fr.theshark34.sender;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import javax.swing.JOptionPane;

/**
 * The Sender ID
 *
 * <p>
 * This class creates and store the Unique Computer ID, it's the last numbers of
 * the local IP
 * </p>
 *
 * @author TheShark34
 * @version RELEASE-1.0
 */
public class SenderID {

	/**
	 * The generated ID, this is the last numbers of the local IP
	 */
	public static final String ID = createID();

	/**
	 * Create the ID by the last number of the local IP
	 * 
	 * @return The created ID
	 */
	private static String createID() {
		try {
			// Getting the network interfaces
			Enumeration<NetworkInterface> n = NetworkInterface
					.getNetworkInterfaces();

			// For each interface
			while (n.hasMoreElements()) {
				// Getting it
				NetworkInterface e = n.nextElement();

				// Getting its addresses
				Enumeration<InetAddress> a = e.getInetAddresses();

				// For each address
				while (a.hasMoreElements()) {
					// Getting it
					InetAddress addr = a.nextElement();

					// If this is the local IP
					if (addr.getHostAddress().contains("192.168.")) {
						// Converting it to an ID and returning it
						return ipToID(addr.getHostAddress());
					}
				}
			}
		} catch (SocketException e) {
			// If it failed to get the interfaces, printing the exception
			e.printStackTrace();

			// Printing an error message
			JOptionPane.showMessageDialog(null,
					"Unable to resolve your local IP address, can't get your network interfaces\n"
							+ e, "Error", JOptionPane.ERROR_MESSAGE);

			// Exiting
			System.exit(1);
		}

		// If nothing was found, printing a message
		JOptionPane.showMessageDialog(null,
				"Unable to resolve your local IP address, it wasn't found\n",
				"Error", JOptionPane.ERROR_MESSAGE);

		// And exiting
		System.exit(1);

		// Can't happen ^^
		return null;
	}

	/**
	 * Converts an IP to an ID
	 * 
	 * @param ip
	 *            The IP to convert
	 * @return The generated ID
	 */
	public static String ipToID(String ip) {
		// Splitting the ip by the .
		String[] strs = ip.split("\\.");

		// Returning the last numbers
		return strs[2] + strs[3];
	}

	/**
	 * Converts an ID to an IP
	 * 
	 * @param id
	 *            The ID to convert
	 * @return The generated IP
	 */
	public static String idToIP(String id) {
		return "192.168." + id.substring(0, 1) + "." + id.substring(1, 3);
	}

}
