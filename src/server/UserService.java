package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class UserService {

	private static final String FILE_NAME = "users.txt";

	public static synchronized boolean authenticate(String username, String password) {
		boolean usernameFound = false;

		try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
			String line;

			while ((line = reader.readLine()) != null) {
				String[] parts = line.split(";", 2);

				if (parts.length != 2) {
					continue;
				}

				String savedUsername = parts[0].trim();
				String savedPassword = parts[1].trim();

				if (savedUsername.equals(username)) {
					usernameFound = true;

					if (savedPassword.equals(password)) {
						return true;
					}

					return false;
				}
			}

		} catch (IOException e) {
			System.err.println("Erreur lors de la lecture : " + e.getMessage());
			return false;
		}

		if (!usernameFound) {
			return addUser(username, password);
		}

		return false;
	}

	private static boolean addUser(String username, String password) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
			writer.write(username + ";" + password);
			writer.newLine();

			return true;

		} catch (IOException e) {
			System.err.println("Erreur lors de l'écriture : " + e.getMessage());
			return false;
		}
	}
}
