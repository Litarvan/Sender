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
package fr.theshark34.sender.cmd;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * The Graphical Zipper
 * 
 * <p>
 * This class contains all things related to the zips. It cans index and zip
 * files, and unzip a file. It also updates the progress bar and the info label
 * of the info frame.
 * </p>
 *
 * @author TheShark34
 * @version RELEASE-1.0
 */
public class CmdZipper {

	/**
	 * All files to zip
	 */
	private static ArrayList<File> files;

	/**
	 * The folder where the files to be sent are located
	 */
	private static File baseFolder;

	/**
	 * The final zip file
	 */
	private static File zipFile;

	public static void indexAndZip(final File[] filesToAdd, final int ID) {
		// Creating the list
		files = new ArrayList<File>();

		// Creating a Thread
		Thread t = new Thread() {
			@Override
			public void run() {
				// Setting base folder
				baseFolder = filesToAdd[0].getParentFile();

				// Creating the file list
				System.out.println("Indexing...");
				addToList(filesToAdd);

				// Zipping all files in the list
				zipAll();
				
				// Starting the client
				CmdClient cmdClient = new CmdClient();
				cmdClient.sendFile(zipFile, String.valueOf(ID));
			}
		};
		t.start();
	}

	/**
	 * Zip all files in the list
	 */
	private static void zipAll() {
		try {
			// Printing the numbers of files
			System.out.println(files.size() + " files to zip");

			// Creating the output streams
			zipFile = getZipFile();
			FileOutputStream fos = new FileOutputStream(zipFile);
			ZipOutputStream zos = new ZipOutputStream(fos);

			// For each files in the list
			for (int i = 0; i < files.size(); i++) {
				// Getting it
				File file = files.get(i);

				// Getting the percentage
				double percentage = (double) i / (double) files.size() * 100;

				// Printing a message
				System.out.print("Zipping..... "
						+ ((int) percentage < 10 ? " " : "") + (int) percentage
						+ "%\r");

				// Only if it's not a directory
				if (!file.isDirectory()) {
					// Reading it and zipping it
					FileInputStream fis = new FileInputStream(file);
					String filePath = file.getAbsolutePath().substring(
							baseFolder.getAbsolutePath().length() + 1,
							file.getAbsolutePath().length());
					ZipEntry zipEntry = new ZipEntry(filePath);
					zos.putNextEntry(zipEntry);
					byte[] bytes = new byte[1024];
					int length;
					while ((length = fis.read(bytes)) >= 0)
						zos.write(bytes, 0, length);

					// Closing entry and input stream
					zos.closeEntry();
					fis.close();
				}
			}

			// Printing a message
			System.out.println("Terminated !\n");

			// Closing outputs streams
			zos.close();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Add all files of an Array to the list
	 * 
	 * @param filesToAdd
	 *            The Array of the files to add
	 */
	private static void addToList(File[] filesToAdd) {
		// For each file in the array
		for (File f : filesToAdd) {
			// If it's a directory adding all its content
			if (f.isDirectory())
				addToList(f.listFiles());
			else
				// Else adding it to the list
				files.add(f);
		}
	}

	/**
	 * Create the temporary zip file
	 * 
	 * @return The created zip file
	 */
	private static File getZipFile() {
		// Creating the .Sender folder and the zip file
		File folder = new File(System.getProperty("user.home") + "/.Sender");
		if (!folder.exists())
			folder.mkdirs();
		File zipFile = new File(folder, "zipTemp.zip");

		// Delete it if it already exists
		if (zipFile.exists())
			zipFile.delete();
		return zipFile;
	}

	public static void unzip() throws IOException {
		// Get the received file
		File folder = new File(System.getProperty("user.home") + "/.Sender");
		final File zipFile = new File(folder, "receivedZipTemp.zip");

		// Getting the number of entries
		ZipFile zip = new ZipFile(zipFile);
		int numberOfEntries = zip.size();
		zip.close();

		// Creating streams
		ZipInputStream zis = new ZipInputStream(new BufferedInputStream(
				new FileInputStream(zipFile)));
		ZipEntry ze = null;

		// Number of loops
		int i = 0;

		// For each zip entry
		while ((ze = zis.getNextEntry()) != null) {
			// Adding one to i
			i++;

			// Getting the output file
			File f = new File(ze.getName());

			// Getting the percentage
			double percentage = (double) i / (double) numberOfEntries * 100;

			// Printing it
			System.out.print("Unzipping... "
					+ ((int) percentage < 10 ? " " : "") + (int) percentage
					+ "%\r");

			// If it is a directory just make it and continue
			if (ze.isDirectory()) {
				f.mkdirs();
				continue;
			}

			// Make all parents directory of the file
			if(f.getParentFile() != null)
				f.getParentFile().mkdirs();

			// Write the file
			OutputStream fos = new BufferedOutputStream(new FileOutputStream(f));
			final byte[] buf = new byte[8192];
			int bytesRead;
			while (-1 != (bytesRead = zis.read(buf)))
				fos.write(buf, 0, bytesRead);
			fos.close();
		}
		
		// Closing the input stream
		zis.close();
		
		// Printing a message
		System.out.print("Terminated !");
	}

}
