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
 * The "Choose the files destination" frame
 *
 * @author TheShark34
 * @version ALPHA-0.0.1
 */
@SuppressWarnings("serial")
public class ChooseOutDirFrame extends JFrame {

	/**
	 * The "Choose the files destination" label
	 */
	private JLabel label;

	/**
	 * The text field
	 */
	private JTextField textField;

	/**
	 * The OK button
	 */
	private JButton button;

	public ChooseOutDirFrame() {
		// Setting things
		this.setTitle("Sender");
		setSize(400, 55);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(null);
		setUndecorated(true);
		setLocationRelativeTo(null);

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

		// Initializing and adding components
		label = new JLabel("Choose the files destination : ");
		label.setForeground(new Color(255, 255, 255));
		label.setBounds(15, 10, 150, 35);
		add(label);
		textField = new JTextField();
		textField.setText(System.getProperty("user.home")
				+ "/Documents/Sender Downloads");
		textField.setBounds(165, 15, 150, 24);
		add(textField);
		button = new JButton("OK");
		button.setBounds(325, 15, 60, 24);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Sender.instance.setOutputDir(new File(textField.getText()));
				setVisible(false);
			}
		});
		add(button);
	}

	/**
	 * Display a new Frame
	 */
	public static void displayFrame() {
		// Initializing frame
		ChooseOutDirFrame codf = new ChooseOutDirFrame();

		// Setting it visible
		codf.setVisible(true);
	}

}
