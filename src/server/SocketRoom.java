package server;

import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.IOException;

public class SocketRoom {
	
	private static class Client {
		int clientId;
		DataOutputStream out;
		
		public Client(int clientId, DataOutputStream out) {
			this.clientId = clientId;
			this.out = out;
		}
	}
	
	private ArrayList<Client> clients = new ArrayList<Client>();
	
	public SocketRoom() {}
	
	public synchronized void addClient(int clientId, DataOutputStream out) {
		clients.add(new Client(clientId, out));
	}
	
	public synchronized void removeClient(int clientId) {
		clients.removeIf(client -> client.clientId == clientId);
	}
	
	public synchronized void sendMessage(int clientId, String message) {
		
		for (Iterator<Client> i = clients.iterator(); i.hasNext();) {
			
			Client client = i.next();
			if (client.clientId == clientId) continue;
			
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