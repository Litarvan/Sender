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

import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

/**
 * The info Frame
 *
 * @author TheShark34
 * @version ALPHA-0.0.1
 */
@SuppressWarnings("serial")
public class InfoFrame extends JFrame {

	/**
	 * Where the mouse clicked
	 */
	private Point initialClick;

	/**
	 * The ID label
	 */
	private JLabel id;

	/**
	 * The progress bar
	 */
	private JProgressBar pb;

	/**
	 * The info label
	 */
	private JLabel infoLabel;

	/**
	 * Basic constructor
	 */
	public InfoFrame() {
		// Setting things
		this.setTitle("Sender");
		this.setSize(400, 200);
		this.setUndecorated(true);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);

		// Setting the icon
		this.setIconImage(Sender.images[0]);

		// Setting a new content pane with background
		this.setContentPane(new JPanel() {
			@Override
			public void paintComponent(Graphics g) {
				g.drawImage(Sender.images[8], 0, 0, this);
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
		id = new JLabel("Your ID : " + Sender.ID, SwingConstants.CENTER);
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
