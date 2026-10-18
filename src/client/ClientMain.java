package client;

import java.util.Scanner;
import common.AuthenticationCode;
import common.InputValidator;
import java.net.Socket;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientMain {
	
	private static final String QUIT_COMMAND = "/quit";
	
	private static final String[] authenticationMessages = {
		"Connexion réussie",
		"Utilisateur déjà connecté",
		"Identifiants incorrects",
		"Le serveur a rencontré un problème..."
	};
	
	private static final AtomicBoolean wasClosedVoluntarily = new AtomicBoolean();

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		
		while(true) {
			String serverIpAddress = InputValidator.readIpAddress(scanner, "Entrez l'adresse IP du serveur :");
			int    serverPort      = InputValidator.readPort(scanner, "Entrez le port du serveur :");
			
			try (Socket socket = new Socket(serverIpAddress, serverPort);
				DataOutputStream out = new DataOutputStream(socket.getOutputStream());
				DataInputStream in = new DataInputStream(socket.getInputStream())) {
				
				wasClosedVoluntarily.set(false);
				String username, password;
				
				do {
					username = InputValidator.readUsername(scanner, "Entrez votre nom d'utilisateur :");
					password = InputValidator.readPassword(scanner, "Entrez votre mot de passse :");
					
					out.writeUTF(username);
					out.writeUTF(password);
					out.flush();
					
					AuthenticationCode code = AuthenticationCode.values()[in.readInt()];
					System.out.println(authenticationMessages[code.ordinal()]);
					
					if (code == AuthenticationCode.SUCCESSFUL_AUTHENTICATION) {
						break;
					}
					
					if (code == AuthenticationCode.SERVER_ERROR) {
						return;
					}
				} while(true);
				
				// set up a seperate thread for messages coming from the server
				ServerHandler serverHandler = new ServerHandler(in, wasClosedVoluntarily);
				Thread serverHandlerThread = new Thread(serverHandler);
				serverHandlerThread.start();
				
				try {
					while (!socket.isClosed()) {
						String message = InputValidator.readMessage(scanner, null);
						if (message.equals(QUIT_COMMAND)) {
							wasClosedVoluntarily.set(true);
							socket.close();
							break;
						}
						out.writeUTF(message);
						out.flush();
					}
				} catch(IOException e) { /*ServerHandler already informed the user about the disconnection*/ }
			}
			catch(IOException e) {
				System.err.println("Message d'erreur: " + e.getMessage());
			}
		}
	}
}
