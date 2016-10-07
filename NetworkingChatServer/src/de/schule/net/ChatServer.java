package de.schule.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

public class ChatServer implements ClientEventReceiver, Runnable {
	public static final int Port = 25552;
	
	// Indicates whether the server is listening for connections
	private boolean mIsListening;
	
	// Contains the listening socket
	private ServerSocket mServerSocket;
	
	// Contains all connected clients
	private List<ChatClientHandler> mClientConnections;
	
	public ChatServer(){
		mClientConnections = new ArrayList<>();
	}
	
	

	@Override
	public void run() {
		try {
			// Start the server
			listenForConnections();
		} catch (IOException e) {
			
			// When an exception was thrown we shutdown the server
			shutdownServer();
		}
	}
	
	/*
	 * Listens for client connections
	 * */
	public void listenForConnections() throws IOException{
		mServerSocket = new ServerSocket(Port);
	    
		mIsListening = true;
		
		while(mIsListening){
			// Accept the connection
			Socket clientSocket = mServerSocket.accept();
			
			
			System.out.println("Connection from " + clientSocket.getRemoteSocketAddress().toString());
			
			// Setup a new client handler to handle the client
			ChatClientHandler connection = new ChatClientHandler(clientSocket.getRemoteSocketAddress().toString(), this);
			
			connection.registerEventReceiver(this);
			connection.setupConnection(clientSocket);
			connection.startListeningForMessages();
			
			// Add the the list of connected clients
			mClientConnections.add(connection);
		}
	}
	
	/*
	 * Stops the server
	 * */
	public void shutdownServer(){
		mIsListening = false;
		
		// Close all client connections
		for (int i = 0; i < mClientConnections.size(); i++) {
			mClientConnections.get(i).closeConnection();
		}
		
		
		// Try to close the server socket
		if(mServerSocket != null){
			try {
				mServerSocket.close();
			} catch (IOException e) { }
			
			mServerSocket = null;
		}
	}

	/*
	 * Counts the connected clients
	 * */
	public int getClientCount(){
		return mClientConnections.size();
	}

	@Override
	public void OnMessageReceived(ChatMessage message, ChatClientHandler connection) {
		System.out.println("Message received: " + message.getMessage());
		
		// Send the received message to all the other clients
		for (ChatClientHandler chatClientConnection : mClientConnections) {
			if(chatClientConnection != connection){
				chatClientConnection.sendMessage(message);
			}
		}
	}

	@Override
	public void OnClientDisconnected(ChatClientHandler connection) {
		System.out.println("Client with IP " + connection.getClientIp() + " disconnected");
		
		// Remove the disconnected client
		if(mIsListening){
			mClientConnections.remove(connection);
		}
	}
}
