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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * The Graphical Sender
 * 
 * <p>
 * So this is the main graphical class. This is the main frame. It loads all the
 * images, displays an instance of the main frame, and displays a
 * {@link ChooseOutDirFrame} instance. It also contains the port, and the
 * current state, and getters for all the stuff.
 * </p>
 *
 * @author TheShark34
 * @version RELEASE-1.0
 */
@SuppressWarnings("serial")
public class GraphicalSender extends JFrame {

	/**
	 * The current Sender instance
	 */
	public static GraphicalSender instance;

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
	 * The output directory
	 */
	private File outputDir;

	/**
	 * The base constructor
	 */
	public GraphicalSender() {
		// Setting instance as this
		GraphicalSender.instance = this;

		// Setting base things
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setTitle("Sender");
		this.setSize(200, 200);
		this.setUndecorated(true);
		this.setBackground(new Color(0, 0, 0, 0));

		// Setting the icon
		this.setIconImage(images[10]);

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
						g.drawImage(images[0], 0, 0, this);
					else
						g.drawImage(images[1], 0, 0, this);
					break;
				case ZIPPED:
					g.drawImage(images[1], 0, 0, this);
					break;
				case SENDING:
					String time1 = Long.toString(System.currentTimeMillis());
					int millis1 = Integer.parseInt(time1.substring(
							time1.length() - 3, time1.length()));
					if (millis1 - 500 < 0)
						g.drawImage(images[2], 0, 0, this);
					else
						g.drawImage(images[3], 0, 0, this);
					break;
				case RECEIVING:
					String time2 = Long.toString(System.currentTimeMillis());
					int millis2 = Integer.parseInt(time2.substring(
							time2.length() - 3, time2.length()));
					if (millis2 - 500 < 0)
						g.drawImage(images[4], 0, 0, this);
					else
						g.drawImage(images[5], 0, 0, this);
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
						GraphicalZipper.indexAndZip(files);
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
	 * This starts the graphical sender.
	 * 
	 * @throws IOException
	 *             If it failed to load the images or things like this
	 * @throws UnsupportedLookAndFeelException
	 *             If it failed to load the system look and feel
	 * @throws IllegalAccessException
	 *             Same
	 * @throws InstantiationException
	 *             Same
	 * @throws ClassNotFoundException
	 *             Same
	 */
	public static void start() throws IOException, ClassNotFoundException,
			InstantiationException, IllegalAccessException,
			UnsupportedLookAndFeelException {
		// Setting the system LookAndFeel
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		// Starting Server
		GraphicalServer.startServer();

		// Reading all images
		images = new Image[11];
		images[0] = ImageIO.read(GraphicalSender.class
				.getResourceAsStream("/images/img1.png"));
		images[1] = ImageIO.read(GraphicalSender.class
				.getResourceAsStream("/images/img2.png"));
		images[2] = ImageIO.read(GraphicalSender.class
				.getResourceAsStream("/images/img3.png"));
		images[3] = ImageIO.read(GraphicalSender.class
				.getResourceAsStream("/images/img4.png"));
		images[4] = ImageIO.read(GraphicalSender.class
				.getResourceAsStream("/images/img5.png"));
		images[5] = ImageIO.read(GraphicalSender.class
				.getResourceAsStream("/images/img6.png"));
		images[6] = ImageIO.read(GraphicalSender.class
				.getResourceAsStream("/images/plus.png"));
		images[7] = ImageIO.read(GraphicalSender.class
				.getResourceAsStream("/images/moins.png"));
		images[8] = ImageIO.read(GraphicalSender.class
				.getResourceAsStream("/images/bg.png"));
		images[9] = ImageIO.read(GraphicalSender.class
				.getResourceAsStream("/images/bgmini.png"));
		images[10] = ImageIO.read(GraphicalSender.class
				.getResourceAsStream("/images/icon.png"));

		// Starting Sender
		new GraphicalSender();
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
