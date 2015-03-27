package fr.theshark34.sender.cmd;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import fr.theshark34.sender.Main;
import fr.theshark34.sender.SenderID;

public class CmdServer {

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

					// Printing a message
					System.out.println("\nREADY : Waiting for a connection...");

					// Listen for a connection
					listen();
				} catch (BindException e) {
					// If it failed to bind the port, printing a message
					System.out
							.println("Unable to launcher the server, a program is using the ports (maybe Sender is already open) : "
									+ e);

					// Printing the error
					e.printStackTrace();

					// And exiting
					System.exit(1);
				} catch (IOException e) {
					// If it failed to start the server, printing a message
					System.out.println("Unable to launch the server : " + e);

					// Printing the error
					e.printStackTrace();

					// And exiting
					System.exit(1);
				}
			}
		};
		t.start();

		// Creating a scanner to stop the server
		Scanner sc = new Scanner(System.in);

		// While true
		while (true) {
			// If the user typed stop
			if (sc.nextLine().equals("stop")) {
				// Closing the scanner
				sc.close();

				// Printing a message
				System.out.println("\nGoodbye !");

				// Exiting
				System.exit(0);
			}

			// Else
			else
				// Printing an error message
				System.out.println("ERROR: Unknown command");
		}
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

						// Printing a message
						System.out
								.println("\nConnection received from computer with ID "
										+ SenderID.ipToID(socket
												.getInetAddress()
												.getHostAddress()));

						try {
							// Receiving file
							receive(client);

							// Unzip received file
							CmdZipper.unzip();
						} catch (IOException e) {
							// Printing a message
							System.out.println("Unable to receive the file : "
									+ e + "\n");

							// Printing the error
							e.printStackTrace();

							// And exiting
							System.exit(1);
						} finally {
							// Setting receiving to false
							receiving = false;
						}

						// Printing a message
						System.out
								.println("\nServer is ready to receive new files");
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
		// Printing a message
		System.out.println("Preparing...");

		// Creating .Sender folder if it doesn't exist
		File folder = new File(System.getProperty("user.home") + "/.Sender/");
		if (!folder.exists())
			folder.mkdirs();

		// Initializing streams
		InputStream input = client.getInputStream();
		OutputStream output = client.getOutputStream();

		// Sending to client that we're ready
		output.write("go#".getBytes());

		// Printing a message
		System.out.println("Waiting for the client...");

		// Receiving file size
		String str = "";
		char c;
		while ((c = (char) input.read()) != '#')
			str += c;

		int fileSize = Integer.parseInt(str);

		// Printing a message
		System.out.println("File size is " + fileSize / 1000000 + " MB");

		// Creating destination file
		File file = new File(folder, "receivedZipTemp.zip");
		new File(file.getParent()).mkdirs();

		// Receiving file
		OutputStream fileOutput = new FileOutputStream(file);
		byte buf[] = new byte[1024];
		int n;
		int received = 0;

		// Printing a message
		System.out.print("Receiving...  0%\r");

		while ((n = input.read(buf)) != -1) {
			// Write received buffer
			fileOutput.write(buf, 0, n);

			// Getting the percentage
			double percentage = (double) (received += n) / (double) fileSize
					* 100;

			// Printing it
			System.out.print("Receiving... "
					+ ((int) percentage < 10 ? " " : "") + (int) percentage
					+ "%\r");
		}

		// Printing a message
		System.out.println("Terminated !");

		// Closing outputs
		fileOutput.close();
		output.close();
		input.close();
		client.close();
	}

}
