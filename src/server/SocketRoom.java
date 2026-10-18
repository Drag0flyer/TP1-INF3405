package server;

import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.IOException;

public class SocketRoom {
	
	private static class Client {
		String username;
		DataOutputStream out;
		
		public Client(String username, DataOutputStream out) {
			this.username = username;
			this.out = out;
		}
	}
	
	private ArrayList<Client> clients = new ArrayList<Client>();
	
	public SocketRoom() {}
	
	public synchronized void addClient(String username, DataOutputStream out) {
		clients.add(new Client(username, out));
	}
	
	public synchronized void removeClient(String username) {
		clients.removeIf(client -> client.username.equals(username));
	}
	
	public synchronized void sendMessage(String username, String message) {
		
		for (Iterator<Client> i = clients.iterator(); i.hasNext();) {
			
			Client client = i.next();
			if (client.username.equals(username)) continue;
			
			try {
				client.out.writeUTF(message);
			}
			catch(IOException e) {
				i.remove();
				System.out.println("Erreur dans SocketRoom: " + e.getMessage());
			}
		}
	}
}