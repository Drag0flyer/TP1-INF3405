package common;

import java.util.Scanner;

// Validation des entrées du client et du serveur de clavardage.
public final class InputValidator {

	public static final int MIN_PORT = 5000;
	public static final int MAX_PORT = 5050;
	public static final int MAX_MESSAGE_LENGTH = 200;

	// Séparateur utilisé dans le fichier users.txt.
	public static final String FILE_SEPARATOR = ";";

	private InputValidator() {
	}

	
	/// 1. Message d'erreur précis, ou null si valide ///

	// Adresse IP : 4 nombres de 0 à 255 séparés par des points. 
	public static String getIpAddressError(String ipAddress) {
		if (ipAddress == null || ipAddress.trim().isEmpty()) {
			return "L'adresse IP ne peut pas être vide.";
		}

		String[] ipParts = ipAddress.trim().split("\\.", -1);
		if (ipParts.length != 4) {
			return "L'adresse IP doit comporter exactement 4 blocs séparés "
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

	// Port : nombre entier entre MIN_PORT et MAX_PORT inclusivement.
	public static String getPortError(String portText) {
		if (portText == null || portText.trim().isEmpty()) {
			return "Le port ne peut pas être vide.";
		}
		String trimmedPort = portText.trim();
		// Au plus 5 chiffres : évite aussi un dépassement de la taille d'un int
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

	// Message de clavardage : non vide et au plus 200 caractères.
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

	// Nom d'utilisateur : non vide, sans espace au début ni à la fin, et sans le séparateur du fichier
	public static String getUsernameError(String username) {
		return getUserInfoError(username, "Le nom d'utilisateur");
	}

	// Mot de passe : mêmes règles que le nom d'utilisateur. Les espaces au début et à la fin sont refusés
	public static String getPasswordError(String password) {
		return getUserInfoError(password, "Le mot de passe");
	}

	// Règles partagées au nom d'utilisateur et au mot de passe.
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


	/// 2. Tests : true si valide, false sinon ///

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


	/// 3. Saisie au clavier ///

	// Retourne une adresse IP valide, sans espaces.
	public static String readIpAddress(Scanner scanner, String prompt) {
		while (true) {
			System.out.print(prompt);
			String input = scanner.nextLine().trim();
			String error = getIpAddressError(input);
			if (error == null) {
				return input;
			}
			System.out.println("Erreur : " + error);
		}
	}

	// Retourne un numéro de port valide.
	public static int readPort(Scanner scanner, String prompt) {
		while (true) {
			System.out.print(prompt);
			String input = scanner.nextLine().trim();
			String error = getPortError(input);
			if (error == null) {
				return Integer.parseInt(input);
			}
			System.out.println("Erreur : " + error);
		}
	}

	// Retourne un nom d'utilisateur valide sans espaces.
	public static String readUsername(Scanner scanner, String prompt) {
		while (true) {
			System.out.print(prompt);
			String input = scanner.nextLine().trim();
			String error = getUsernameError(input);
			if (error == null) {
				return input;
			}
			System.out.println("Erreur : " + error);
		}
	}

	// Retourne un mot de passe valide (pris tel quel, sans retirer d'espaces).
	public static String readPassword(Scanner scanner, String prompt) {
		while (true) {
			System.out.print(prompt);
			String input = scanner.nextLine();
			String error = getPasswordError(input);
			if (error == null) {
				return input;
			}
			System.out.println("Erreur : " + error);
		}
	}
}