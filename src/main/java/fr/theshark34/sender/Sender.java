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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * The main class
 *
 * @author TheShark34
 * @version ALPHA-0.0.1
 */
@SuppressWarnings("serial")
public class Sender extends JFrame {

	/**
	 * The current Sender instance
	 */
	public static Sender instance;

	/**
	 * All the images
	 */
	public static Image[] images;

	/**
	 * The current state, can be WAITING, ZIPPING, SENDING or RECEIVING
	 */
	private int state = 0;

	/**
	 * The state when Sender is waiting for a file to send/receive
	 */
	public static final int WAITING = 0;

	/**
	 * The state when Sender is indexing files after drag 'n' drop
	 */
	public static final int ZIPPING = 1;

	/**
	 * The state when Sender is waiting for the user to type the ID
	 */
	public static final int ZIPPED = 2;

	/**
	 * The state when Sender is sending file
	 */
	public static final int SENDING = 3;

	/**
	 * The state when Sender is receiving files
	 */
	public static final int RECEIVING = 4;

	/**
	 * The port for sockets
	 */
	public static final int PORT = 24538;

	/**
	 * The +/- icon
	 */
	private JLabel plus;

	/**
	 * The info frame
	 */
	private InfoFrame iframe;

	/**
	 * Where the user clicked
	 */
	private Point initialClick;

	/**
	 * The ID
	 */
	public static final String ID = createID();

	/**
	 * The output directory
	 */
	private File outputDir;

	/**
	 * The base constructor
	 */
	public Sender() {
		// Setting instance as this
		Sender.instance = this;

		// Setting base things
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setTitle("Sender");
		this.setSize(200, 200);
		this.setUndecorated(true);
		this.setBackground(new Color(0, 0, 0, 0));

		// Setting the icon
		this.setIconImage(images[0]);

		// Setting a new content pane
		this.setContentPane(new JPanel() {

			@Override
			public void paintComponent(Graphics g) {
				// The panel will draw a specific image according to the state
				switch (state) {
				case WAITING:
					g.drawImage(images[0], 0, 0, null);
					break;
				case ZIPPING:
					String time = Long.toString(System.currentTimeMillis());
					int millis = Integer.parseInt(time.substring(
							time.length() - 3, time.length()));
					if (millis - 500 < 0)
						g.drawImage(images[0], 0, 0, null);
					else
						g.drawImage(images[1], 0, 0, null);
					break;
				case ZIPPED:
					g.drawImage(images[1], 0, 0, null);
					break;
				case SENDING:
					String time1 = Long.toString(System.currentTimeMillis());
					int millis1 = Integer.parseInt(time1.substring(
							time1.length() - 3, time1.length()));
					if (millis1 - 500 < 0)
						g.drawImage(images[2], 0, 0, null);
					else
						g.drawImage(images[3], 0, 0, null);
					break;
				case RECEIVING:
					String time2 = Long.toString(System.currentTimeMillis());
					int millis2 = Integer.parseInt(time2.substring(
							time2.length() - 3, time2.length()));
					if (millis2 - 500 < 0)
						g.drawImage(images[4], 0, 0, null);
					else
						g.drawImage(images[5], 0, 0, null);
					break;
				}
			}

		});
		this.getContentPane().setLayout(null);

		// Initializing Drag 'n' Drop system
		new FileDrop(this.getContentPane(), new FileDrop.Listener() {
			public void filesDropped(final java.io.File[] files) {
				if (state != WAITING)
					return;
				// Create a new Thread to don't stop repaint loop
				Thread t = new Thread() {
					@Override
					public void run() {
						Zipper.indexAndZip(files);
					}
				};
				t.start();
			}
		});

		// Initializing info frame
		iframe = new InfoFrame();

		// Initializing plus label
		plus = new JLabel();
		plus.setIcon(new ImageIcon(images[6]));
		plus.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				iframe.setVisible(!iframe.isVisible());
				if (iframe.isVisible())
					plus.setIcon(new ImageIcon(images[7]));
				else
					plus.setIcon(new ImageIcon(images[6]));
			}
		});
		plus.setBounds(168, 168, 32, 32);
		this.getContentPane().add(plus);

		// Setting mouse listeners
		this.getContentPane().addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				initialClick = e.getPoint();
			}
		});
		this.getContentPane().addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				int thisX = getLocation().x;
				int thisY = getLocation().y;
				int xMoved = (thisX + e.getX()) - (thisX + initialClick.x);
				int yMoved = (thisY + e.getY()) - (thisY + initialClick.y);
				int X = thisX + xMoved;
				int Y = thisY + yMoved;
				setLocation(X, Y);
			}
		});

		// Initializing other things
		this.getContentPane().setBackground(new Color(0, 0, 0, 0));
		this.setLocationRelativeTo(null);
		this.setVisible(true);

		// This Thread will repaint the frame each 0.5 sec to update the image
		new Thread() {
			public void run() {
				while (true) {
					try {
						sleep(500L);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					repaint();
				}
			}
		}.start();

		// Open a "Choose the files destination" frame
		ChooseOutDirFrame.displayFrame();
	}

	/**
	 * The main method
	 * 
	 * @param args
	 *            Useless
	 * @throws IOException
	 *             If it fails to read an image
	 */
	public static void main(String[] args) throws IOException {
		// Setting the system LookAndFeel
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		// Starting Server
		Server.startServer();

		// Reading all images
		images = new Image[10];
		images[0] = ImageIO.read(Sender.class
				.getResourceAsStream("/images/img1.png"));
		images[1] = ImageIO.read(Sender.class
				.getResourceAsStream("/images/img2.png"));
		images[2] = ImageIO.read(Sender.class
				.getResourceAsStream("/images/img3.png"));
		images[3] = ImageIO.read(Sender.class
				.getResourceAsStream("/images/img4.png"));
		images[4] = ImageIO.read(Sender.class
				.getResourceAsStream("/images/img5.png"));
		images[5] = ImageIO.read(Sender.class
				.getResourceAsStream("/images/img6.png"));
		images[6] = ImageIO.read(Sender.class
				.getResourceAsStream("/images/plus.png"));
		images[7] = ImageIO.read(Sender.class
				.getResourceAsStream("/images/moins.png"));
		images[8] = ImageIO.read(Sender.class
				.getResourceAsStream("/images/bg.png"));
		images[9] = ImageIO.read(Sender.class
				.getResourceAsStream("/images/bgmini.png"));

		// Starting Sender
		new Sender();
	}

	/**
	 * Create the ID
	 * 
	 * @return The created ID
	 */
	private static String createID() {
		try {
			Enumeration<NetworkInterface> n = NetworkInterface
					.getNetworkInterfaces();
			for (; n.hasMoreElements();) {
				NetworkInterface e = n.nextElement();

				Enumeration<InetAddress> a = e.getInetAddresses();
				for (; a.hasMoreElements();) {
					InetAddress addr = a.nextElement();
					if (addr.getHostAddress().contains("192.168.1.")) {
						String[] strs = addr.getHostAddress().split("\\.");
						return strs[3];
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Set a new state (WAITING, SENDING, etc...)
	 * 
	 * @param state
	 *            The new state
	 */
	public void setCurrentState(int state) {
		this.state = state;
	}

	/**
	 * Return the info frame
	 */
	public InfoFrame getInfoFrame() {
		return this.iframe;
	}

	/**
	 * Return the output directory
	 * 
	 * @return The output directory
	 */
	public File getOutputDir() {
		return this.outputDir;
	}

	/**
	 * Set a new output directory
	 * 
	 * @param outputDir
	 *            The new output directory
	 */
	public void setOutputDir(File outputDir) {
		this.outputDir = outputDir;
	}
}
