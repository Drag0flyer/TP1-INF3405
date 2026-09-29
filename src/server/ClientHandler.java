package server;

import common.NetworkInputHelper;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler implements Runnable {

	private static final Map<String, ClientHandler> activeClients = Collections.synchronizedMap(new HashMap<>()); // car
																													// multithread,
																													// si
																													// dex
																													// therad
																													// set
																													// en
																													// meme
																													// temps,
																													// il
																													// n'y
																													// aura
																													// pas
																													// de
																													// pprbleme
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd@HH:mm:ss");
	private static final ZoneId ZONE_ID = ZoneId.of("America/Montreal");
	private static final String HISTORY_FILE = "history.txt";

	private final Socket clientSocket;
	private DataOutputStream out;
	private String clientIp;
	private int clientPort;
	private String currentUsername;
	private boolean running = true;

	public ClientHandler(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}

	@Override
	public void run() {
		try (Socket s = this.clientSocket;
				DataInputStream in = new DataInputStream(s.getInputStream());
				DataOutputStream outStream = new DataOutputStream(s.getOutputStream())) {

			this.out = outStream;

			if (authentication(in, outStream)) {
				activeClients.put(this.currentUsername, this);
				sendHistory();
				chatLoop(in);
			}

		} catch (IOException e) {
			System.err.println("Déconnexion client (" + clientSocket.getInetAddress() + ") : " + e.getMessage());
		} finally {
			this.running = false;
			if (currentUsername != null) {
				activeClients.remove(currentUsername);
			}
		}
	}

	private boolean authentication(DataInputStream in, DataOutputStream out) throws IOException {
		String ip = in.readUTF().trim();
		String portStr = in.readUTF().trim();
		String username = in.readUTF().trim();
		String password = in.readUTF().trim();

		if (!NetworkInputHelper.isValidIp(ip)) {
			out.writeUTF("Erreur : IP invalide.");
			out.flush();
			return false;
		}

		int port;
		try {
			port = Integer.parseInt(portStr);
			if (!NetworkInputHelper.isValidPort(port, 5000, 5050)) {
				out.writeUTF("Erreur : Port hors plage.");
				out.flush();
				return false;
			}
		} catch (NumberFormatException e) {
			out.writeUTF("Erreur : Port invalide.");
			out.flush();
			return false;
		}

		if (username.contains(" ") || password.contains(" ") || username.contains(";") || password.contains(";")) {
			out.writeUTF(
					"Erreur : le pseudo et le mot de passe ne doivent pas contenir d'espaces ni de point-virgules.");
			out.flush();
			return false;
		}

		if (activeClients.containsKey(username)) {
			out.writeUTF("Erreur : Utilisateur déjà connecté.");
			out.flush();
			return false;
		}

		if (!UserService.authenticate(username, password)) {
			out.writeUTF("Erreur : Mot de passe incorrect.");
			out.flush();
			return false;
		}

		this.clientIp = ip;
		this.clientPort = port;
		this.currentUsername = username;

		out.writeUTF("Connexion réussie");
		out.flush();
		return true;
	}

	private void chatLoop(DataInputStream in) throws IOException {
		while (running) {
			String line = in.readUTF().trim();

			// Il fadrais voir avec le client, pour linstant e emte cette coommande pour
			// quitter la boucle infini
			if (line.equalsIgnoreCase("/quit")) {
				break;
			}

			broadcast(this, line);
		}
	}

	// besoin de synchronized car sinon un probleme si deux personnes ecrivent en
	// meme temps
	public synchronized void sendMessage(String message) {
		if (out != null) {
			try {
				out.writeUTF(message);
				out.flush();
			} catch (IOException e) {
				System.err.println("Erreur d'envoi du message: " + e.getMessage());
			}
		}
	}

	// Static synchronized pour que sur toute la classe ya pas plus de 1 ecriture en
	// meme temps sur history.txt
	private static synchronized void saveToHistory(String formattedMessage) {
		try (FileWriter fileWriter = new FileWriter(HISTORY_FILE, true);
				BufferedWriter writer = new BufferedWriter(fileWriter)) {

			writer.write(formattedMessage);
			writer.newLine();

		} catch (IOException e) {
			System.err.println("Erreur sauvegarde historique : " + e.getMessage());
		}
	}

	private void sendHistory() {
		File file = new File(HISTORY_FILE);

		if (!file.exists()) {
			return;
		}

		List<String> lines = new ArrayList<>();

		try (FileReader fileReader = new FileReader(file); BufferedReader reader = new BufferedReader(fileReader)) {

			String line = reader.readLine();

			while (line != null) {
				lines.add(line);
				line = reader.readLine();
			}

		} catch (IOException e) {
			System.err.println("Erreur lecture historique : " + e.getMessage());
			return;
		}

		int start;
		if (lines.size() > 15) {
			start = lines.size() - 15; // logique pour les 15 derniers lignes ou moins si l'historique est plus petit
										// que 15
		} else {
			start = 0;
		}

		for (int i = start; i < lines.size(); i++) {
			sendMessage(lines.get(i));
		}
	}

	public static void broadcast(ClientHandler sender, String message) {
		String timestamp = ZonedDateTime.now(ZONE_ID).format(FORMATTER);
		String formattedMessage = String.format("[%s - %s:%d - %s] : %s", sender.currentUsername, sender.clientIp,
				sender.clientPort, timestamp, message);

		// debogage
		System.out.println(formattedMessage);

		saveToHistory(formattedMessage);

		// car c'est une synchronizedMap
		synchronized (activeClients) {
			for (ClientHandler client : activeClients.values()) {
				client.sendMessage(formattedMessage);
			}
		}
	}
}