package server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
	private final Socket clientSocket;

	public ClientHandler(Socket clientsocket) {
		this.clientSocket = clientsocket;
	}

	@Override
	public void run() {
		authentication();
		
	}
	
	private void authentication() {
		try (Socket socket = this.clientSocket;
			DataInputStream in = new DataInputStream(socket.getInputStream());
			DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {
			
			String username = in.readUTF();
			String password = in.readUTF();

			boolean authenticated = UserService.authenticate(username, password);

			if (authenticated) {
				out.writeUTF("Connexion réussie");
			} else {
				out.writeUTF("Identifiants incorrects");
			}

			out.flush();

		} catch (IOException e) {
			System.err.println("Erreur avec le client : " + e.getMessage());
			}
	}
}
