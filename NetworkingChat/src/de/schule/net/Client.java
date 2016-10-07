package de.schule.net;

import java.util.Scanner;

public class Client implements EventReceiver {
	private ChatClient mClient;
	
	
	public Client(ChatClient client){
		mClient = client;
	}

	@Override
	public void OnMessageReceived(ChatMessage message) {
		System.out.println("Received message from " + message.getUsername() + ": " + message.getMessage());
	}

	@Override
	public void OnConnected(String ip) {
		System.out.println("Connected to server at " + ip);
		
		Scanner scanner = new Scanner(System.in);
		
		while(true){
			String message = scanner.nextLine();
			
			if(message.equalsIgnoreCase("/q")){
				mClient.closeConnection();
				
				scanner.close();
				break;
			}
			else{
				ChatMessage chat = new ChatMessage();
				chat.setMessage(message);
				mClient.sendMessage(chat);
			}
		}
	}

	@Override
	public void OnDisconnected() {
		System.out.println("Disconnected from server");
	}
	
	
	public static void main(String[] args) {
		Scanner s = new Scanner(System.in);
		System.out.print("Benutzername: ");
		
		
		ChatClient client = new ChatClient("localhost", s.nextLine());
		
		client.registerEventReceiver(new Client(client));
		
		client.connectToServer();
		
	}
}
