package de.schule.net;

import java.util.Scanner;

public class Server {

	public static void main(String[] args) {
		ChatServer server = new ChatServer();

		// Start the server in a new thread
		Thread serverThread = new Thread(server);
		serverThread.start();
		
		// Wait for user input before shutdown
		Scanner s = new Scanner(System.in);
		s.nextLine();
		
		server.shutdownServer();
		
		s.close();
		
		System.out.println("Server stopped");
	}
}
