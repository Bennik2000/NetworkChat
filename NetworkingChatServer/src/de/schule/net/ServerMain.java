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
		
		while(true){
			String line = s.nextLine();
			
			if(line.startsWith("/")){
				String command = line.substring(1);
				String[] arguments = command.split("[ ]+");
				
				if(arguments[0].equals("exit")){
					break;
				}
				if(arguments[0].equals("ban")){
					if(arguments.length < 2){
						System.out.println("Too few arguments");
					}
					else{
						server.kickClientIpByName(arguments[1], true);
					}
				}
				if(arguments[0].equals("kick")){
					if(arguments.length < 2){
						System.out.println("Too few arguments");
					}
					else{
						server.kickClientIpByName(arguments[1], false);
					}
				}
			}
		}
		
		server.shutdownServer();
		
		s.close();
		
		System.out.println("Server stopped");
	}
}
