package server;

import common.NetworkInputHelper;
import java.util.Scanner;

public class ServerMain {

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		
		String ipAdress = NetworkInputHelper.readIpAddress(scanner);
        int portNumber = NetworkInputHelper.readPort(scanner, 5000, 5050);
		
		
	}

}
