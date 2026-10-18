package server;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileNotFoundException;
import common.InputValidator;

public class MessageService {

	private static final String FILE_NAME = "messages.txt";
	private static final int MAX_RECENT_MESSAGES = 15;
	private static final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
	
	public static String[] getRecentMessages() {
		String[] messages = {};
		rwl.readLock().lock();
		try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
			// read messages
			ArrayList<String> allMessages = new ArrayList<String>();
			String line;
			while((line = reader.readLine()) != null) {
				allMessages.add(line);
			}
			
			// only keep the most recent messages
			int nMessagesInExcess = allMessages.size() - MAX_RECENT_MESSAGES;
			if (nMessagesInExcess < 0) {
				messages = allMessages.toArray(messages);
			} else {
				messages = new String[MAX_RECENT_MESSAGES];
				for (int i = 0; i < MAX_RECENT_MESSAGES; ++i) {
					messages[i] = allMessages.get(i + nMessagesInExcess);
				}
			}
		}
		catch(FileNotFoundException e) { /* Ignore this error. There's no 'problem', there's just no saved messages. */ }
		catch(IOException e) { System.err.println("Erreur lors de la lecture : " + e.getMessage()); }
		finally { rwl.readLock().unlock(); }
		return messages;
	}
	
	public static boolean saveMessage(String message) {
		rwl.writeLock().lock();
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
			writer.write(message);
			writer.newLine();
			return true;
		}
		catch(FileNotFoundException e) { System.err.println("Fichier inaccessible : " + e.getMessage()); }
		catch(IOException e) { System.err.println("Erreur lors de l'écriture : " + e.getMessage()); }
		finally { rwl.writeLock().unlock(); }
		return false;
	}
}
