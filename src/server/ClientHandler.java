package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import common.AuthenticationCode;
import common.InputValidator;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;


public class ClientHandler implements Runnable {
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd@HH:mm:ss");
	
	private final SocketRoom room;
	private final Socket clientSocket;

	public ClientHandler(SocketRoom room, Socket clientsocket) {
		this.room = room;
		this.clientSocket = clientsocket;
	}

	@Override
	public void run() {
		try (Socket socket = this.clientSocket;
			DataInputStream in = new DataInputStream(socket.getInputStream());
			DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {
			
			String username, password;
			
			// Authenticate user. If the credentials are wrong or the user is already connected, ask new credentials.
			do {
				try {
					username = in.readUTF();
					password = in.readUTF();
				} catch(IOException e) {
					System.out.println("Déconnexion d'un utilisateur encore non-authentifié");
					return;
				}
				
				AuthenticationCode code;
				if (!InputValidator.isValidUsername(username) || !InputValidator.isValidPassword(password)) {
					code = AuthenticationCode.INVALID_CREDENTIALS;
				} else {
					code = UserService.authenticate(username, password);					
				}
				
				try {
					out.writeInt(code.ordinal());
					out.flush();
				} catch (IOException e) {
					System.out.println("Déconnexion d'un utilisateur encore non-authentifié");
					UserService.disconnectUser(username); // The authentication could have been valid. Don't take chances.
					return;
				}
				
				if (code == AuthenticationCode.SUCCESSFUL_AUTHENTICATION) {
					System.out.println("Utilisateur " + username + " authentifié");
					break;
				}
				
				if (code == AuthenticationCode.SERVER_ERROR) {
					System.err.println("Le serveur a rencontré une erreur an tentant d'authentifier l'utilisateur : " + username);
					return;
				}
			} while (true);
			
			
			try {
				// Send the most recent messages to the new user.
				for(String message : MessageService.getRecentMessages()) {
					out.writeUTF(message);
				}
				
				// Let the users converse together in a room.
				room.addClient(username, out);
				
				String clientIpAddress = socket.getRemoteSocketAddress().toString();
				String message;
				
				while(true) {
					message = in.readUTF();
					if (InputValidator.isValidMessage(message)) {
						message = formatMessage(username, clientIpAddress, message);
						MessageService.saveMessage(message);
						room.sendMessage(username, message);
					}
				}
			}
			catch(IOException e) { System.out.println("L'utilisateur " + username + " s'est déconnecté"); }
			
			room.removeClient(username);
			UserService.disconnectUser(username);

		} catch (IOException e) {
			System.err.println("Erreur dans ClientHandler : " + e.getMessage());
		}
	}
	
	private static String formatMessage(String username, String clientIpAddress, String message) {
		String timestamp = ZonedDateTime.now().format(FORMATTER);
		return String.format("[%s - %s - %s] : %s", username, clientIpAddress, timestamp, message);
	}
}
