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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * The client
 *
 * @author TheShark34
 * @version ALPHA-0.0.1
 */
@SuppressWarnings("serial")
public class TypeIDFrame extends JFrame {

	/**
	 * The "Enter the receiver's ID" label
	 */
	private JLabel enterIDLabel;

	/**
	 * The textField where the ID will be typed
	 */
	private JTextField idTextField;

	/**
	 * The "OK" Button
	 */
	private JButton okButton;

	/**
	 * Basic constructor
	 */
	public TypeIDFrame(final File zipFile) {
		// Setting things
		this.setSize(400, 55);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(null);
		this.setUndecorated(true);
		this.setLocationRelativeTo(null);

		// Setting the icon
		this.setIconImage(Sender.images[0]);

		// Setting a new content pane with background
		this.setContentPane(new JPanel() {
			@Override
			public void paintComponent(Graphics g) {
				g.drawImage(Sender.images[9], 0, 0, this);
			}
		});
		this.getContentPane().setLayout(null);
		this.getContentPane().setBackground(new Color(219, 87, 0));

		// Initializing label
		enterIDLabel = new JLabel("Enter the receiver's ID : ");
		enterIDLabel.setForeground(new Color(255, 255, 255));
		enterIDLabel.setBounds(15, 10, 150, 35);
		this.add(enterIDLabel);

		// Initializing text field
		idTextField = new JTextField();
		idTextField.setBounds(200, 15, 75, 24);
		this.add(idTextField);

		// Initializing button
		okButton = new JButton("OK");
		okButton.setBounds(300, 15, 66, 24);
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				TypeIDFrame.this.setVisible(false);
				Client c = new Client();
				c.sendFile(zipFile, idTextField.getText());
			}
		});
		this.add(okButton);
	}

	/**
	 * Display a "Enter the receiver's ID" frame
	 * 
	 * @param zipFile
	 *            The zip file to send
	 */
	public static void displayFrame(File zipFile) {
		// Inizializing frame
		TypeIDFrame tidf = new TypeIDFrame(zipFile);

		// Setting it visible
		tidf.setVisible(true);
	}

}
