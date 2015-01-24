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
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * A class that zip files
 *
 * @author TheShark34
 * @version ALPHA-0.0.1
 */
public class Zipper {

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

	public static void indexAndZip(final File[] filesToAdd) {
		// Setting Sender state to ZIPPING
		Sender.instance.setCurrentState(Sender.ZIPPING);

		// Changing info label
		Sender.instance.getInfoFrame().setInfo("Zipping");

		// Creating the list
		files = new ArrayList<File>();

		// Creating a Thread
		Thread t = new Thread() {
			@Override
			public void run() {
				// Changing info label
				Sender.instance.getInfoFrame().setInfo("Listing files");

				// Setting base folder
				baseFolder = filesToAdd[0].getParentFile();

				// Creating the file list
				System.out.println("Creating file list");
				addToList(filesToAdd);

				// Zipping all files in the list
				System.out.println("Zipping");
				zipAll();

				// Setting Sender state to ZIPPED
				System.out.println("Done");
				Sender.instance.setCurrentState(Sender.ZIPPED);

				// Displaying ID choosing frame
				TypeIDFrame.displayFrame(zipFile);
			}
		};
		t.start();
	}

	/**
	 * Zip all files in the list
	 */
	private static void zipAll() {
		try {
			// Creating the output streams
			zipFile = getZipFile();
			FileOutputStream fos = new FileOutputStream(zipFile);
			ZipOutputStream zos = new ZipOutputStream(fos);

			// Setting the bar maximum
			Sender.instance.getInfoFrame().getProgressBar()
					.setMaximum(files.size());

			// For each files in the list
			for (int i = 0; i < files.size(); i++) {
				// Setting the bar value
				Sender.instance.getInfoFrame().getProgressBar().setValue(i);

				// Changing info label
				Sender.instance.getInfoFrame().setInfo(
						"Zipping files " + (i + 1) + "/" + files.size());

				File file = files.get(i);

				// Only if it's not a directory
				if (!file.isDirectory()) {
					System.out.println("Adding to zip : "
							+ file.getAbsolutePath());
					// Read it and zip it
					FileInputStream fis = new FileInputStream(file);
					String filePath = file.getAbsolutePath().substring(
							baseFolder.getAbsolutePath().length() + 1,
							file.getAbsolutePath().length());
					ZipEntry zipEntry = new ZipEntry(filePath);
					zos.putNextEntry(zipEntry);
					byte[] bytes = new byte[1024];
					int length;
					while ((length = fis.read(bytes)) >= 0) {
						zos.write(bytes, 0, length);
					}

					// Close entry and input stream
					zos.closeEntry();
					fis.close();
				}
			}

			// Close outputs streams
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
			// If it's a directory add all its content
			if (f.isDirectory())
				addToList(f.listFiles());
			else {
				// Else add it to the list
				System.out.println("Adding : " + f.getAbsolutePath());
				files.add(f);
			}
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

		// Setting Sender state
		Sender.instance.setCurrentState(Sender.ZIPPING);

		// Setting info label
		Sender.instance.getInfoFrame().setInfo("Unzipping");

		// Creating streams
		ZipInputStream zis = new ZipInputStream(new BufferedInputStream(
				new FileInputStream(zipFile)));
		ZipEntry ze = null;

		// For each zip entry
		while ((ze = zis.getNextEntry()) != null) {
			File f = new File(Sender.instance.getOutputDir(), ze.getName());

			// If it is a directory just make it and continue
			if (ze.isDirectory()) {
				f.mkdirs();
				continue;
			}

			// Make all parents directory of the file
			f.getParentFile().mkdirs();

			// Write the file
			OutputStream fos = new BufferedOutputStream(new FileOutputStream(f));
			final byte[] buf = new byte[8192];
			int bytesRead;
			while (-1 != (bytesRead = zis.read(buf)))
				fos.write(buf, 0, bytesRead);
			fos.close();
		}
		zis.close();
	}

}
