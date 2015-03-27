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

import java.io.IOException;

import javax.swing.UnsupportedLookAndFeelException;

import fr.theshark34.sender.cmd.CmdSender;
import fr.theshark34.sender.graphical.GraphicalSender;

/**
 * The Sender Main Class
 * 
 * <p>
 * So this is the main sender class. It starts the graphical Sender if there
 * isn't any argument, else it starts the command line Sender.
 * </p>
 *
 * @author TheShark34
 * @version RELEASE-1.0
 */
public class Main {

	/**
	 * The main method
	 * 
	 * @param args
	 *            The given argument
	 */
	public static void main(String[] args) {
		// If there isn't any argument
		if (args.length == 0)
			// Starting graphical sender
			try {
				GraphicalSender.start();
			} catch (ClassNotFoundException e) {
			} catch (InstantiationException e) {
			} catch (IllegalAccessException e) {
			} catch (IOException e) {
			} catch (UnsupportedLookAndFeelException e) {
			}
		else
			CmdSender.start(args);
	}

	/**
	 * The port for sockets
	 */
	public static final int PORT = 24538;

}
