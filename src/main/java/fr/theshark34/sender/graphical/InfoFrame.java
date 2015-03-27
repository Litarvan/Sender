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

import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

import fr.theshark34.sender.SenderID;

/**
 * The Info Frame
 * 
 * <p>
 * This Frame is displayed when clicking the '+' button on the Sender frame, it
 * has bg.png in background and contains informations like the computer ID, a
 * progress bar and a label for the current task.
 * </p>
 *
 * @author TheShark34
 * @version RELEASE-1.0
 */
@SuppressWarnings("serial")
public class InfoFrame extends JFrame {

	/**
	 * Where the mouse clicked, used to move the frame from this point
	 */
	private Point initialClick;

	/**
	 * A label that shows the computer ID
	 */
	private JLabel id;

	/**
	 * The progress bar of the current task
	 */
	private JProgressBar pb;

	/**
	 * The info label that shows the current task
	 */
	private JLabel infoLabel;

	/**
	 * Basic constructor that displays the frame
	 */
	public InfoFrame() {
		// Setting things
		this.setTitle("Sender");
		this.setSize(400, 200);
		this.setUndecorated(true);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);

		// Setting the icon
		this.setIconImage(GraphicalSender.images[10]);

		// Setting a new content pane with background
		this.setContentPane(new JPanel() {
			@Override
			public void paintComponent(Graphics g) {
				g.drawImage(GraphicalSender.images[8], 0, 0, this);
			}
		});
		this.getContentPane().setLayout(null);

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

		// Initializing the ID label
		id = new JLabel("Your ID : " + SenderID.ID, SwingConstants.CENTER);
		id.setFont(id.getFont().deriveFont(18f));
		id.setBounds(0, 30, 400, 20);
		this.getContentPane().add(id);

		// Initializing the info label
		infoLabel = new JLabel("Waiting", SwingConstants.CENTER);
		infoLabel.setFont(infoLabel.getFont().deriveFont(18f));
		infoLabel.setBounds(0, 70, 400, 20);
		this.getContentPane().add(infoLabel);

		// Initializing the progress bar
		pb = new JProgressBar();
		pb.setBounds(15, 130, 370, 15);
		this.getContentPane().add(pb);
	}

	/**
	 * Change text of the info label
	 * 
	 * @param info
	 *            The new text
	 */
	public void setInfo(String info) {
		this.infoLabel.setText(info);
	}

	/**
	 * Return the progress bar
	 * 
	 * @return The progress bar
	 */
	public JProgressBar getProgressBar() {
		return this.pb;
	}

}
