package server;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class UserService {

	private static final String FILE_NAME = "users.txt";

	public static synchronized boolean authenticate(String username, String password) {
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
					return savedPassword.equals(password);
				}
			}

			return addUser(username, password); // Le fichier existe, mais l'utilisateur n'existe pas encore donc creation d'utilisateur

		} catch (FileNotFoundException e) {
			return addUser(username, password); // Le fichier n'existe pas, donc création du premier utilisateur
			
		} catch (IOException e) {
			System.err.println("Erreur lors de la lecture : " + e.getMessage());
			return false;
		}
	}

	private static boolean addUser(String username, String password) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
			writer.write(username + ";" + password);
			writer.newLine();

			return true;

		} catch (FileNotFoundException e) {
			System.err.println("Fichier inaccessible : " + e.getMessage());
			return false;

		} catch (IOException e) {
			System.err.println("Erreur lors de l'écriture : " + e.getMessage());
			return false;
		}
	}
}
