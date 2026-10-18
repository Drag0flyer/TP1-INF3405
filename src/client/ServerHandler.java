package client;

import java.io.DataInputStream;
import java.io.IOException;

public class ServerHandler implements Runnable {
	private final DataInputStream in;
	
	public ServerHandler(DataInputStream serverStream) {
		this.in = serverStream;
	}
	
	@Override
	public void run() {
		try {
			do {
				System.out.print(in.readUTF() + '\n');
			}
			while(true);
		} catch(IOException e) {
			System.err.println("La connexion avec le serveur a été rompue. Entrez 'ok' dans la console pour revenir à l'étape de connection.");
			try { in.close(); }
			catch (IOException c) { System.err.println("Erreur lors de la fermeture du DataInputStream : " + c.getMessage()); }
		}
	}
}
