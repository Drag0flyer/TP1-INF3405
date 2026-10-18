package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Calendar;
import common.AuthenticationCode;

public class ClientHandler implements Runnable {
	private final int clientId;
	private final SocketRoom room;
	private final Socket clientSocket;

	public ClientHandler(int clientId, SocketRoom room, Socket clientsocket) {
		this.clientId = clientId;
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
				
				AuthenticationCode code = UserService.authenticate(username, password);
				
				try {
					out.writeInt(code.ordinal());
					out.flush();
				} catch (IOException e) {
					System.out.println("Déconnexion d'un utilisateur encore non-authentifié");
					UserService.disconnectUser(username);
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
				room.addClient(clientId, out);
				
				String clientIpAddress = socket.getRemoteSocketAddress().toString();
				String message;
				
				while(true) {
					message = in.readUTF();
					message = formatMessageHeader(username, clientIpAddress, Calendar.getInstance()).append(message).toString();
					room.sendMessage(clientId, message);
					MessageService.saveMessage(message);
				}
			}
			catch(IOException e) { System.out.println("L'utilisateur " + username + " s'est déconnecté"); }
			
			room.removeClient(clientId);
			UserService.disconnectUser(username);

		} catch (IOException e) {
			System.err.println("Erreur dans ClientHandler : " + e.getMessage());
		}
	}
	
	private StringBuffer formatMessageHeader(String username, String clientIpAddress, Calendar currentTime) {
		int year = currentTime.get(Calendar.YEAR);
		int month = currentTime.get(Calendar.MONTH);
		int day = currentTime.get(Calendar.DAY_OF_MONTH);
		int hour = currentTime.get(Calendar.HOUR_OF_DAY);
		int minute = currentTime.get(Calendar.MINUTE);
		int second = currentTime.get(Calendar.SECOND);
		StringBuffer header = new StringBuffer(100);
		header.append('[');
		header.append(username);
		header.append(" - ");
		header.append(clientIpAddress);
		header.append(" - ");
		header.append(year);
		header.append('-');
		header.append(month);
		header.append('-');
		header.append(day);
		header.append('@');
		header.append(hour);
		header.append(':');
		header.append(minute);
		header.append(':');
		header.append(second);
		header.append("] : ");
		return header;
	}
}
