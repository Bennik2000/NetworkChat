package de.benedikt;

import java.util.Scanner;

public class Server {

	public static void main(String[] args) {
		ChatServer server = new ChatServer();

		Thread serverThread = new Thread(server);
		serverThread.start();
		
		Scanner s = new Scanner(System.in);
		s.nextLine();
		
		server.shutdownServer();
		
		s.close();
		
		System.out.println("Server stopped");
	}
}
