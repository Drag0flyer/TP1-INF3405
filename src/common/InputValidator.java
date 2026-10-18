package common;

import java.util.Scanner;

// Validation des entrées : client et serveur
public final class InputValidator {

	public static final int MIN_PORT = 5000;
	public static final int MAX_PORT = 5050;
	public static final int MAX_MESSAGE_LENGTH = 200;

	// Séparateur
	public static final String FILE_SEPARATOR = ";";

	private InputValidator() {
	}

	
/// 1. Message d'erreur

	// Adresse IP
	public static String getIpAddressError(String ipAddress) {
		if (ipAddress == null || ipAddress.trim().isEmpty()) {
			return "L'adresse IP ne peut pas être vide.";
		}

		String[] ipParts = ipAddress.trim().split("\\.", -1);
		if (ipParts.length != 4) {
			return "L'adresse IP doit avoir exactement 4 blocs séparés "
					+ "par des points (ex. 192.168.1.25).";
		}

		for (String part : ipParts) {
			// Uniquement 1 à 3 chiffres : refuse "", "abc", "+1", "-1", "1 2"
			if (!part.matches("\\d{1,3}")) {
				return "Chaque partie de l'adresse IP doit être un nombre "
						+ "entier (reçu : \"" + part + "\").";
			}
			int value = Integer.parseInt(part);
			if (value > 255) {
				return "Chaque partie de l'adresse IP doit être comprise "
						+ "entre 0 et 255 (reçu : " + value + ").";
			}
		}
		return null;
	}

	// Port
	public static String getPortError(String portText) {
		if (portText == null || portText.trim().isEmpty()) {
			return "Le port ne peut pas être vide.";
		}
		String trimmedPort = portText.trim();
	
		if (!trimmedPort.matches("\\d{1,5}")) {
			return "Le port doit être un nombre entier (reçu : \"" + trimmedPort + "\").";
		}
		int portNumber = Integer.parseInt(trimmedPort);
		if (portNumber < MIN_PORT || portNumber > MAX_PORT) {
			return "Le port doit être compris entre " + MIN_PORT + " et "
					+ MAX_PORT + " (reçu : " + portNumber + ").";
		}
		return null;
	}

	// Message 
	public static String getMessageError(String message) {
		if (message == null || message.isBlank()) {
			return "Le message ne peut pas être vide.";
		}
		if (message.length() > MAX_MESSAGE_LENGTH) {
			return "Le message ne peut pas dépasser " + MAX_MESSAGE_LENGTH
					+ " caractères (longueur actuelle : " + message.length() + ").";
		}
		return null;
	}

	// Nom d'utilisateur
	public static String getUsernameError(String username) {
		return getUserInfoError(username, "Le nom d'utilisateur");
	}

	// Mot de passe
	public static String getPasswordError(String password) {
		return getUserInfoError(password, "Le mot de passe");
	}

	// Règles partagées
	private static String getUserInfoError(String entry, String label) {
		if (entry == null || entry.isEmpty()) {
			return label + " ne peut pas être vide.";
		}
		if (!entry.equals(entry.trim())) {
			return label + " ne peut pas commencer ni se terminer par un espace.";
		}
		if (entry.contains(FILE_SEPARATOR)) {
			return label + " ne peut pas contenir le caractère \""
					+ FILE_SEPARATOR + "\".";
		}
		return null;
	}


/// 2. Tests

	public static boolean isValidIpAddress(String ipAddress) {
		return getIpAddressError(ipAddress) == null;
	}

	public static boolean isValidPort(String portText) {
		return getPortError(portText) == null;
	}

	public static boolean isValidMessage(String message) {
		return getMessageError(message) == null;
	}

	public static boolean isValidUsername(String username) {
		return getUsernameError(username) == null;
	}

	public static boolean isValidPassword(String password) {
		return getPasswordError(password) == null;
	}


/// 3. Saisie

	// Adresse IP
	public static String readIpAddress(Scanner scanner, String prompt) {
		while (true) {
			if (prompt != null) { System.out.println(prompt); }
			String input = scanner.nextLine().trim();
			String error = getIpAddressError(input);
			if (error == null) {
				return input;
			}
			System.out.println("Erreur : " + error);
		}
	}

	// Port
	public static int readPort(Scanner scanner, String prompt) {
		while (true) {
			if (prompt != null) { System.out.println(prompt); }
			String input = scanner.nextLine().trim();
			String error = getPortError(input);
			if (error == null) {
				return Integer.parseInt(input);
			}
			System.out.println("Erreur : " + error);
		}
	}

	// Nom d'utilisateur
	public static String readUsername(Scanner scanner, String prompt) {
		while (true) {
			if (prompt != null) { System.out.println(prompt); }
			String input = scanner.nextLine().trim();
			String error = getUsernameError(input);
			if (error == null) {
				return input;
			}
			System.out.println("Erreur : " + error);
		}
	}

	// Mot de passe
	public static String readPassword(Scanner scanner, String prompt) {
		while (true) {
			if (prompt != null) { System.out.println(prompt); }
			String input = scanner.nextLine();
			String error = getPasswordError(input);
			if (error == null) {
				return input;
			}
			System.out.println("Erreur : " + error);
		}
	}
	
	// Message
	public static String readMessage(Scanner scanner, String prompt) {
		while(true) {
			if (prompt != null) { System.out.println(prompt); }
			String input = scanner.nextLine();
			String error = getMessageError(input);
			if (error == null) {
				return input;
			}
			System.out.print("Erreur : " + error);
		}
	}
}
