package de.schule.net;

import java.util.Scanner;

public class ServerMain {

	public static void main(String[] args) {
		System.out.println("Starting server...");
		
		ChatServer server = new ChatServer();

		// Start the server in a new thread
		Thread serverThread = new Thread(server);
		serverThread.start();

		System.out.println("Listening for connections on port " + ChatServer.Port);
		
		// Wait for user input before shutdown
		Scanner s = new Scanner(System.in);
		s.nextLine();
		
		server.shutdownServer();
		
		s.close();
		
		System.out.println("Server stopped");
	}
}
