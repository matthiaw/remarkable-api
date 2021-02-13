/*
 * Remarkable Console - Copyright (C) 2021 Matthias Wegner
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package org.rogatio.remarkable.api;

import static org.rogatio.remarkable.api.io.PropertiesCache.DEVICETOKEN;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.rogatio.remarkable.api.io.PropertiesCache;
import org.rogatio.remarkable.api.io.RemarkableClient;

public class Starter {

	/** The Constant logger. */
	private static final Logger logger = LogManager.getLogger(Starter.class);

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {

		// delete old log files
		for (File f : new File(".").listFiles()) {
			if (f.getName().endsWith(".log")) {
				f.delete();
			}
		}

		// instantiates the remarkable manager
		RemarkableManager rm = auth();

		// download templates via ssh from remarkable (need both in same network)
		// if templates not exist
		downloadTemplates(rm);

	}

	/**
	 * Authenticate application against remarkable web application.
	 *
	 * @return the remarkable manager
	 */
	private static RemarkableManager auth() {
		// check if deviceToken for this remarkable client exists
		boolean deviceTokenExists = PropertiesCache.getInstance().propertyExists(DEVICETOKEN);

		if (!deviceTokenExists) {
			// instantiates remarkable client
			RemarkableClient rc = new RemarkableClient();

			// prepare input for one-time-code
			System.out.println(
					"Device not registered yet. Please input OneTimeCode from https://my.remarkable.com/connect/desktop:");
			Scanner sc = new Scanner(System.in);
			try {
				// get new created device token for this client
				String createdToken = rc.newDeviceToken(sc.nextLine().trim());

				if (createdToken != null) {
					// write token to client
					PropertiesCache.getInstance().setProperty(DEVICETOKEN, createdToken);
					PropertiesCache.getInstance().flush();

					logger.info("New Device Token for client created");
				} else {
					logger.error("Device Token not found. Skip.");
					System.exit(0);
				}

			} catch (IOException e) {
				logger.error("Error creating device token");
			} finally {
				sc.close();
			}
		}
		return RemarkableManager.getInstance();
	}

	/**
	 * Download svg templates.
	 *
	 * @param rm the remarkable manager
	 */
	private static void downloadTemplates(RemarkableManager rm) {
		// read template folder from properties
		String templateDir = PropertiesCache.getInstance().getValue(PropertiesCache.TEMPLATEFOLDER);

		// download templates via ssh if not existing
		if (!new File(templateDir).exists()) {

			boolean hostIpExists = PropertiesCache.getInstance().propertyExists(PropertiesCache.SSHHOST);
			boolean hostPswdExists = PropertiesCache.getInstance().propertyExists(PropertiesCache.SSHPSWD);

			if (!hostIpExists || !hostPswdExists) {
				System.out.println(
						"Open in remarkable Settings > About > Copyrights and licenses > General information (scroll down):");
				Scanner sc = new Scanner(System.in);

				if (!hostIpExists) {
					System.out.println("Enter IP-Host-Adress:");
					PropertiesCache.getInstance().setProperty(PropertiesCache.SSHHOST, sc.nextLine().trim());
				}

				if (!hostPswdExists) {
					System.out.println("Enter Password (for root):");
					PropertiesCache.getInstance().setProperty(PropertiesCache.SSHPSWD, sc.nextLine().trim());
				}

				try {
					PropertiesCache.getInstance().flush();
				} catch (FileNotFoundException e) {
					logger.error("Error setting ssh properties", e);
				} catch (IOException e) {
					logger.error("Error setting ssh properties", e);
				} finally {
					sc.close();
				}
			}

			logger.info("New SSH configuration created");

			// download templates with ssh-connection
			rm.downloadTemplates();
		}
	}

}
