package server;

import common.NetworkInputHelper;
import java.net.SocketException;
import java.util.Scanner;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;

public class ServerMain {

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		int portNumber = NetworkInputHelper.readPort(scanner, 5000, 5050);
		scanner.close();

		try (ServerSocket serverSocket = new ServerSocket(portNumber)) {

			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				try {
					if (!serverSocket.isClosed()) {
						serverSocket.close();
					}
				} catch (IOException e) {
					System.err.println("Erreur lors de la fermeture du socket serveur : " + e.getMessage());
				}
				// debogage
				System.out.println("Serveur arrete proprement.");
			}));

			// deboage
			System.out.println("Serveur en ecoute sur le port " + portNumber);

			while (!serverSocket.isClosed()) {
				Socket clientSocket = serverSocket.accept();
				System.out.println("Nouveau client connecté : " + clientSocket.getInetAddress()); // debgage

				ClientHandler clientHandler = new ClientHandler(clientSocket);
				Thread clientThread = new Thread(clientHandler);
				clientThread.start();
			}

		} catch (SocketException e) {
			// Erreur normale quand le shutdown hook force la fermeture du socket pendant
			// accept()
			System.out.println("La boucle d'écoute du serveur a été interrompue.");
		} catch (IOException e) {
			System.err.println("Erreur serveur : " + e.getMessage());
		}
	}
}
