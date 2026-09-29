package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class UserService {

	private static final String FILE_NAME = "users.txt";

	private UserService() {
	}

	public static synchronized boolean authenticate(String username, String password) {
		File file = new File(FILE_NAME);

		if (!file.exists()) {
			return addUser(username, password);
		}

		try (FileReader fr = new FileReader(file); BufferedReader reader = new BufferedReader(fr)) {

			String line = reader.readLine();

			while (line != null) {
				String[] parts = line.split(";", 2);

				if (parts.length != 2) {
					continue;
				}

				String savedUsername = parts[0].trim();
				String savedPassword = parts[1].trim();

				if (savedUsername.equals(username)) {
					return savedPassword.equals(password);
				}

				line = reader.readLine();
			}

			return addUser(username, password);

		} catch (IOException e) {
			System.err.println("Erreur lecture fichier utilisateurs : " + e.getMessage());
			return false;
		}
	}

	private static boolean addUser(String username, String password) {
		try (FileWriter fw = new FileWriter(FILE_NAME, true); BufferedWriter writer = new BufferedWriter(fw)) {

			writer.write(username + ";" + password);
			writer.newLine();
			return true;

		} catch (IOException e) {
			System.err.println("Erreur écriture fichier utilisateurs : " + e.getMessage());
			return false;
		}
	}
}