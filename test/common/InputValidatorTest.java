package common;

import java.util.Scanner;

/**
 * Vérifie le fonctionnement de InputValidator de façon isolée.
 * Dans Eclipse : clic droit sur ce fichier -> Run As -> Java Application.
 */
public class InputValidatorTest {

	private static int failureCount = 0;

	public static void main(String[] args) {

		System.out.println("=== Adresses IP ===");
		check("192.168.1.25", true, InputValidator.isValidIpAddress("192.168.1.25"));
		check("0.0.0.0", true, InputValidator.isValidIpAddress("0.0.0.0"));
		check("255.255.255.255", true, InputValidator.isValidIpAddress("255.255.255.255"));
		check("\" 192.168.1.25 \" (espaces autour)", true, InputValidator.isValidIpAddress(" 192.168.1.25 "));
		check("256.1.1.1 (bloc > 255)", false, InputValidator.isValidIpAddress("256.1.1.1"));
		check("192.168.1 (3 blocs)", false, InputValidator.isValidIpAddress("192.168.1"));
		check("192.168.1.1.1 (5 blocs)", false, InputValidator.isValidIpAddress("192.168.1.1.1"));
		check("192.168.1.1. (point final)", false, InputValidator.isValidIpAddress("192.168.1.1."));
		check(".192.168.1.1 (point initial)", false, InputValidator.isValidIpAddress(".192.168.1.1"));
		check("192.168..1 (bloc vide)", false, InputValidator.isValidIpAddress("192.168..1"));
		check("+1.2.3.4 (signe +)", false, InputValidator.isValidIpAddress("+1.2.3.4"));
		check("abc.def.ghi.jkl", false, InputValidator.isValidIpAddress("abc.def.ghi.jkl"));
		check("(vide)", false, InputValidator.isValidIpAddress(""));
		check("null", false, InputValidator.isValidIpAddress(null));

		System.out.println("\n=== Ports ===");
		check("5000 (borne min)", true, InputValidator.isValidPort("5000"));
		check("5050 (borne max)", true, InputValidator.isValidPort("5050"));
		check("4999", false, InputValidator.isValidPort("4999"));
		check("5051", false, InputValidator.isValidPort("5051"));
		check("abc", false, InputValidator.isValidPort("abc"));
		check("-5000", false, InputValidator.isValidPort("-5000"));
		check("99999999999 (trop grand pour un int)", false, InputValidator.isValidPort("99999999999"));
		check("(vide)", false, InputValidator.isValidPort(""));

		System.out.println("\n=== Messages ===");
		check("\"Bonjour!\"", true, InputValidator.isValidMessage("Bonjour!"));
		check("200 caractères (limite)", true, InputValidator.isValidMessage("a".repeat(200)));
		check("201 caractères", false, InputValidator.isValidMessage("a".repeat(201)));
		check("(vide)", false, InputValidator.isValidMessage(""));
		check("\"   \" (seulement des espaces)", false, InputValidator.isValidMessage("   "));

		System.out.println("\n=== Noms d'utilisateur ===");
		check("alice", true, InputValidator.isValidUsername("alice"));
		check("\"jean tremblay\" (espace au milieu)", true, InputValidator.isValidUsername("jean tremblay"));
		check("(vide)", false, InputValidator.isValidUsername(""));
		check("\"alice \" (espace final)", false, InputValidator.isValidUsername("alice "));
		check("\" alice\" (espace initial)", false, InputValidator.isValidUsername(" alice"));
		check("carl;x (séparateur)", false, InputValidator.isValidUsername("carl;x"));

		System.out.println("\n=== Mots de passe ===");
		check("secret", true, InputValidator.isValidPassword("secret"));
		check("(vide)", false, InputValidator.isValidPassword(""));
		check("\"pw \" (espace final)", false, InputValidator.isValidPassword("pw "));
		check("x;pw (séparateur)", false, InputValidator.isValidPassword("x;pw"));

		System.out.println("\n=== Exemples de messages d'erreur précis ===");
		System.out.println(InputValidator.getIpAddressError("300.1.1.1"));
		System.out.println(InputValidator.getPortError("6000"));
		System.out.println(InputValidator.getUsernameError("carl;x"));

		System.out.println("\n" + (failureCount == 0
				? "Tous les tests automatiques sont réussis."
				: failureCount + " test(s) en ÉCHEC."));

		System.out.println("\n=== Test interactif ===");
		System.out.println("Essaie d'abord des valeurs invalides (ex. Entrée sans rien");
		System.out.println("taper, 192.168.1.1., 6000, alice;x) pour voir les messages.\n");

		Scanner scanner = new Scanner(System.in);
		String ipAddress = InputValidator.readIpAddress(scanner, "Adresse IP : ");
		int portNumber = InputValidator.readPort(scanner,
				"Port (" + InputValidator.MIN_PORT + "-" + InputValidator.MAX_PORT + ") : ");
		String username = InputValidator.readUsername(scanner, "Nom d'utilisateur : ");
		String password = InputValidator.readPassword(scanner, "Mot de passe : ");
		scanner.close();

		System.out.println("\nValeurs acceptées : " + ipAddress + ":" + portNumber
				+ ", utilisateur \"" + username + "\", mot de passe de "
				+ password.length() + " caractères.");
	}

	private static void check(String description, boolean expected, boolean actual) {
		boolean passed = (expected == actual);
		if (!passed) {
			failureCount++;
		}
		System.out.println("[" + (passed ? "OK   " : "ÉCHEC") + "] " + description
				+ " -> attendu=" + expected + ", obtenu=" + actual);
	}
}