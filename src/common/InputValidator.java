package common;

import java.util.Scanner;

/**
 * Validation des entrées du client et du serveur de clavardage.
 *
 * Cette classe regroupe NetworkInputHelper et la validation des messages,
 * des noms d'utilisateur et des mots de passe. Elle est organisée en trois
 * niveaux :
 *
 *   1. Les méthodes "getXxxError(...)" analysent une valeur et retournent un
 *      message d'erreur précis, ou null si la valeur est valide.
 *   2. Les méthodes "isValidXxx(...)" retournent simplement true/false.
 *      Elles peuvent être utilisées partout, y compris côté serveur pour
 *      vérifier une valeur reçue par le réseau.
 *   3. Les méthodes "readXxx(...)" lisent une valeur au clavier et
 *      redemandent la saisie, avec un message d'erreur précis, tant qu'elle
 *      est invalide.
 *
 * Toutes les méthodes sont static : on les appelle sur la classe,
 * par exemple InputValidator.isValidPort("5000").
 */
public final class InputValidator {

	public static final int MIN_PORT = 5000;
	public static final int MAX_PORT = 5050;
	public static final int MAX_MESSAGE_LENGTH = 200;

	/** Séparateur utilisé par UserService dans le fichier users.txt. */
	public static final String FILE_SEPARATOR = ";";

	/** Constructeur privé : cette classe ne sert jamais à créer des objets. */
	private InputValidator() {
	}

	// =====================================================================
	// 1. Analyse : retourne un message d'erreur précis, ou null si valide
	// =====================================================================

	/** Adresse IPv4 : 4 nombres de 0 à 255 séparés par des points. */
	public static String getIpAddressError(String ipAddress) {
		if (ipAddress == null || ipAddress.trim().isEmpty()) {
			return "l'adresse IP ne peut pas être vide.";
		}

		// Le -1 conserve les morceaux vides : "192.168.1.1." donne 5 morceaux
		// au lieu de 4, ce qui permet de refuser le point final.
		String[] ipParts = ipAddress.trim().split("\\.", -1);
		if (ipParts.length != 4) {
			return "l'adresse IP doit comporter exactement 4 blocs séparés "
					+ "par des points (ex. 192.168.1.25).";
		}

		for (String part : ipParts) {
			// Uniquement 1 à 3 chiffres : refuse "", "abc", "+1", "-1", "1 2"
			if (!part.matches("\\d{1,3}")) {
				return "chaque partie de l'adresse IP doit être un nombre "
						+ "entier (reçu : \"" + part + "\").";
			}
			int value = Integer.parseInt(part);
			if (value > 255) {
				return "chaque partie de l'adresse IP doit être comprise "
						+ "entre 0 et 255 (reçu : " + value + ").";
			}
		}
		return null;
	}

	/** Port : nombre entier entre MIN_PORT et MAX_PORT inclusivement. */
	public static String getPortError(String portText) {
		if (portText == null || portText.trim().isEmpty()) {
			return "le port ne peut pas être vide.";
		}
		String trimmedPort = portText.trim();
		// Au plus 5 chiffres : évite aussi un dépassement de la taille d'un int
		if (!trimmedPort.matches("\\d{1,5}")) {
			return "le port doit être un nombre entier (reçu : \"" + trimmedPort + "\").";
		}
		int portNumber = Integer.parseInt(trimmedPort);
		if (portNumber < MIN_PORT || portNumber > MAX_PORT) {
			return "le port doit être compris entre " + MIN_PORT + " et "
					+ MAX_PORT + " (reçu : " + portNumber + ").";
		}
		return null;
	}

	/** Message de clavardage : non vide et au plus 200 caractères. */
	public static String getMessageError(String message) {
		if (message == null || message.isBlank()) {
			return "le message ne peut pas être vide.";
		}
		if (message.length() > MAX_MESSAGE_LENGTH) {
			return "le message ne peut pas dépasser " + MAX_MESSAGE_LENGTH
					+ " caractères (longueur actuelle : " + message.length() + ").";
		}
		return null;
	}

	/**
	 * Nom d'utilisateur : non vide, sans espace au début ni à la fin, et sans
	 * le séparateur du fichier users.txt (sinon la relecture du fichier
	 * confondrait le nom et le mot de passe).
	 */
	public static String getUsernameError(String username) {
		return getCredentialError(username, "le nom d'utilisateur");
	}

	/**
	 * Mot de passe : mêmes règles que le nom d'utilisateur. Les espaces au
	 * début et à la fin sont refusés parce que UserService les retire à la
	 * relecture du fichier, ce qui empêcherait ensuite toute connexion.
	 */
	public static String getPasswordError(String password) {
		return getCredentialError(password, "le mot de passe");
	}

	/** Règles communes au nom d'utilisateur et au mot de passe. */
	private static String getCredentialError(String value, String fieldName) {
		if (value == null || value.isEmpty()) {
			return fieldName + " ne peut pas être vide.";
		}
		if (!value.equals(value.trim())) {
			return fieldName + " ne peut pas commencer ni se terminer par un espace.";
		}
		if (value.contains(FILE_SEPARATOR)) {
			return fieldName + " ne peut pas contenir le caractère \""
					+ FILE_SEPARATOR + "\".";
		}
		return null;
	}

	// =====================================================================
	// 2. Tests simples : true si valide, false sinon
	// =====================================================================

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

	// =====================================================================
	// 3. Saisie au clavier avec nouvelle demande tant que c'est invalide
	// =====================================================================
	// Le Scanner doit être créé une seule fois dans le main et réutilisé.
	// Les erreurs sont affichées sur System.out (et non System.err) pour
	// qu'elles apparaissent toujours dans le bon ordre par rapport aux
	// questions dans la console d'Eclipse.

	/** Retourne une adresse IP valide, sans espaces autour. */
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

	/** Retourne un numéro de port valide. */
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

	/** Retourne un nom d'utilisateur valide (les espaces autour sont retirés). */
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

	/** Retourne un mot de passe valide (pris tel quel, sans retirer d'espaces). */
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