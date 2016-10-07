package de.benedikt;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer implements ClientEventReceiver, Runnable{
	private boolean mIsListening;
	private ServerSocket mServerSocket;
	
	private List<ChatClientHandler> mClientConnections;
	
	public ChatServer(){
		mClientConnections = new ArrayList<>();
	}
	
	

	@Override
	public void run() {
		try {
			listenForConnections();
		} catch (IOException e) { 
			shutdownServer();
		}
	}
	
	public void listenForConnections() throws IOException{
		mServerSocket = new ServerSocket(25552);
	    
		mIsListening = true;
		while(mIsListening){
			Socket clientSocket = mServerSocket.accept();
			
			System.out.println("Connection from " + clientSocket.getRemoteSocketAddress().toString());
			
			ChatClientHandler connection = new ChatClientHandler(clientSocket.getRemoteSocketAddress().toString(), this);
			connection.registerEventReceiver(this);
			connection.setupConnection(clientSocket);
			connection.startListeningForMessages();
			
			mClientConnections.add(connection);
		}
	}
	
	public void shutdownServer(){
		mIsListening = false;
		
		for (int i = 0; i < mClientConnections.size(); i++) {
			mClientConnections.get(i).closeConnection();
		}
		
		if(mServerSocket != null){
			try {
				mServerSocket.close();
			} catch (IOException e) { }
			
			mServerSocket = null;
		}
	}

	public int getMemberCount(){
		return mClientConnections.size();
	}

	@Override
	public void OnMessageReceived(ChatMessage message, ChatClientHandler connection) {
		System.out.println("Message received: " + message.getMessage());
		
		for (ChatClientHandler chatClientConnection : mClientConnections) {
			if(chatClientConnection != connection){
				chatClientConnection.sendMessage(message);
			}
		}
	}

	@Override
	public void OnClientDisconnected(ChatClientHandler connection) {
		System.out.println("Client with IP " + connection.getClientIp() + " disconnected");
		
		if(mIsListening){
			mClientConnections.remove(connection);
		}
	}
}
