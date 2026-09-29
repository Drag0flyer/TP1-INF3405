package common;

import java.util.InputMismatchException;
import java.util.Scanner;

public class NetworkInputHelper {

	private NetworkInputHelper() {
	}

	public static boolean isValidIp(String ipAddress) {
		if (ipAddress == null) {
			return false;
		}
		String[] ipParts = ipAddress.trim().split("\\.");
		if (ipParts.length != 4) {
			return false;
		}

		try {
			for (String p : ipParts) {
				if (p.isEmpty() || (p.length() > 1 && p.startsWith("0"))) {
					return false;
				}
				int part = Integer.parseInt(p);
				if (part < 0 || part > 255) {
					return false;
				}
			}
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public static boolean isValidPort(int port, int min, int max) {
		return port >= min && port <= max;
	}

	public static String readIpAddress(Scanner scanner) {
		// while true pour redemander si invalide
		while (true) {
			System.out.print("Entrez l'adresse IP du poste : ");
			String ipAddress = scanner.nextLine().trim();

			if (isValidIp(ipAddress)) {
				return ipAddress;
			}
			System.err.println("Erreur : l'adresse IP doit comporter 4 blocs d'entiers (0-255) sans zéros initiaux.");
		}
	}

	public static int readPort(Scanner scanner, int min, int max) {
		while (true) {
			System.out.printf("Entrez le port d'ecoute (entre %d et %d) : ", min, max);
			try {
				int port = scanner.nextInt();
				scanner.nextLine(); // vider la ligne

				if (isValidPort(port, min, max)) {
					return port;
				} else {
					System.err.printf("Erreur : le port doit être compris entre %d et %d.%n", min, max);
				}
			} catch (InputMismatchException e) {
				System.err.println("Erreur : vous devez entrer un nombre entier.");
				scanner.nextLine(); // vider la mauvaise saisie
			}
		}
	}
}