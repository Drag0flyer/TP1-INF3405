package common;

import java.util.InputMismatchException;
import java.util.Scanner;


public class NetworkInputHelper {
	private NetworkInputHelper() {}

    public static String readIpAddress(Scanner scanner) {
        while (true) {
            System.out.print("Entrez l'adresse IP du poste : ");
            try {
                String ipAddress = scanner.nextLine().trim();
                String[] ipParts = ipAddress.split("\\.");

                if (ipParts.length != 4) {
                    throw new IllegalArgumentException("L'adresse IP doit comporter exactement 4 blocs.");
                }

                for (String p : ipParts) {
                    int part = Integer.parseInt(p);
                    if (part < 0 || part > 255) {
                        throw new IllegalArgumentException("Chaque partie de l'adresse IP doit être comprise entre 0 et 255 (reçu : " + part + ").");
                    }
                }

                return ipAddress;

            } catch (NumberFormatException e) {
                System.err.println("Erreur : vous devez entrer des nombres entiers entre les points.");
            } catch (IllegalArgumentException e) {
                System.err.println("Erreur : " + e.getMessage());
            }
        }
    }

    public static int readPort(Scanner scanner, int min, int max) {
        while (true) {
            System.out.printf("Entrez le port d'ecoute (entre %d et %d) : ", min, max);
            try {
                int port = scanner.nextInt();
                if (port < min || port > max) {
                    throw new IllegalArgumentException("Port hors limites.");
                }
                
                scanner.nextLine(); // Flush le '\n' restant du nextInt() au cas ou un nextLine() serait appelé plus tard
                
                return port;

            } catch (InputMismatchException e) {
                System.err.println("Erreur : vous devez entrer un nombre entier.");
                scanner.nextLine(); // flush le buffer bloqué
            } catch (IllegalArgumentException e) {
                System.err.printf("Erreur : le port doit être compris entre %d et %d.%n", min, max);
            }
        }
    }
}
