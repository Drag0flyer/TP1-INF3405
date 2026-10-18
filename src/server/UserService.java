package server;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import common.AuthenticationCode;
import java.util.ArrayList;

public class UserService {

	private static final String FILE_NAME = "users.txt";
	
	private static ArrayList<String> connectedUsers = new ArrayList<String>();

	public static synchronized AuthenticationCode authenticate(String username, String password) {
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
					if (!savedPassword.equals(password)) {
						return AuthenticationCode.INVALID_CREDENTIALS;
					}
					
					// verify if the user isn't already connected
					if (connectedUsers.contains(username)) {
						return AuthenticationCode.USER_ALREADY_CONNECTED;
					}
					connectedUsers.add(username);
					return AuthenticationCode.SUCCESSFUL_AUTHENTICATION;
				}
			}

			return addUser(username, password); // Le fichier existe, mais l'utilisateur n'existe pas encore donc creation d'utilisateur

		} catch (FileNotFoundException e) {
			return addUser(username, password); // Le fichier n'existe pas, donc création du premier utilisateur
		} catch (IOException e) {
			System.err.println("Erreur lors de la lecture : " + e.getMessage());
		}
		return AuthenticationCode.SERVER_ERROR;
	}
	
	public static void disconnectUser(String username) {
		connectedUsers.remove(username);
	}

	private static AuthenticationCode addUser(String username, String password) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
			writer.write(username + ";" + password);
			writer.newLine();

			connectedUsers.add(username);
			return AuthenticationCode.SUCCESSFUL_AUTHENTICATION;

		}
		catch (FileNotFoundException e) { System.err.println("Fichier inaccessible : " + e.getMessage()); }
		catch (IOException e) { System.err.println("Erreur lors de l'écriture : " + e.getMessage()); }
		return AuthenticationCode.SERVER_ERROR;
	}
}
