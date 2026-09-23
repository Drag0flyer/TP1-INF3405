package server;

import common.NetworkInputHelper;
import java.util.Scanner;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;

public class ServerMain {

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		
		String ipAdress = NetworkInputHelper.readIpAddress(scanner);
        int portNumber = NetworkInputHelper.readPort(scanner, 5000, 5050);
        
        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            System.out.println("Serveur en écoute sur le port " + portNumber);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nouveau client connecté : " + clientSocket.getInetAddress());

                // Confie le client à un nouveau thread pour ne pas bloquer les autres
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                Thread clientThread = new Thread(clientHandler);
                clientThread.start();
            }

        } catch (IOException e) {
            System.err.println("Erreur serveur : " + e.getMessage());
        }
		
		scanner.close();
	}

}
