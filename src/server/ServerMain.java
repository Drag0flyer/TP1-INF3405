package server;

import common.InputValidator;
import java.util.Scanner;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.net.Inet4Address;

public class ServerMain {

	public static void main(String[] args) {
		SocketRoom room = new SocketRoom();
		Scanner scanner = new Scanner(System.in);
		
		while(true) {
			// ??? I have not found how to construct a SocketAddress from an ipAddress String and give it to serverSocket.
			// Besides, serverSocket prints "0.0.0.0/0.0.0.0" for its InetAddress... I am confused.
			// So, we just ask the user the ipAddress without using it, because the lab's statement says we need to do that.
			String ipAddress = InputValidator.readIpAddress(scanner, "Entrez l'adresse IP du poste : ");
			int portNumber = InputValidator.readPort(scanner, "Entrez le port d'ecoute : ");
			
			try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
				System.out.println("Serveur en écoute sur le port " + portNumber);
				while (true) {
					Socket clientSocket = serverSocket.accept();
					System.out.println("Nouveau client connecté : " + clientSocket.getInetAddress());
					
					// Gives the client to a new thread as to not block the others.
					ClientHandler clientHandler = new ClientHandler(room, clientSocket);
					Thread clientThread = new Thread(clientHandler);
					clientThread.start();
				}
			} catch (IOException e) { System.err.println("Erreur serveur : " + e.getMessage()); }
			scanner.close();
		}
	}
}
