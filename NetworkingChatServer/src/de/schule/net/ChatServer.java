package de.schule.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * The chat server implementation
 * */
public class ChatServer implements EndpointEventReceiver, Runnable {
	public static final int Port = 25552;
	
	private boolean mIsListening;
	
	private ServerSocket mServerSocket;
	
	private List<EndpointHandler> mClientConnections;
	private List<byte[]> mBannedIps;
	
	private JsonPacketHandler mPacketHandler; 
	
	
	public ChatServer(){
		mPacketHandler = new JsonPacketHandler();
		mPacketHandler.registerCommandHandler(new MessageCommandHandler());
		mPacketHandler.registerCommandHandler(new UsernameCommandHandler());
		
		mClientConnections = new ArrayList<>();
		mBannedIps = new ArrayList<>();
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
			
			
			if(isAddressBanned(clientSocket.getInetAddress().getAddress())){
				clientSocket.close();
				continue;
			}
			
			// Setup a new client handler to handle the client
			EndpointHandler connection = new ClientEndpointHandler(mPacketHandler, this);
			
			connection.registerEventReceiver(this);
			connection.setupEndpointConnection(clientSocket);
			
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
			mClientConnections.get(i).disconnectFromEndpoint();
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

	
	/*
	 * Sends a packet to all EndpointHandlers excluding the sourceEndpoint
	 * */
	public void distributePacket(String command, Map<String, String> parameters, EndpointHandler sourceEndpoint){
		for (EndpointHandler endpointHandler : mClientConnections) {
			if(endpointHandler != sourceEndpoint){
				endpointHandler.sendPacket(command, parameters);
			}
		}
	}
	
	/*
	 * Bans a client by its IP
	 * */
	public void kickClientIpByName(String name, boolean ban){
		List<ClientEndpointHandler> clientsToKick = new ArrayList<>();
		
		for (EndpointHandler endpointHandler : mClientConnections) {
			
			// When the end point is a ClientEndpointHandler we can go on
			if (ClientEndpointHandler.class.isInstance(endpointHandler)){
				ClientEndpointHandler client = (ClientEndpointHandler) endpointHandler;
			
				// Check if the username matches
				if(client.getUsername().equalsIgnoreCase(name)){
					
					if(ban){
						// Add the ip to the list
						mBannedIps.add(client.getEndpointAddress());
					}
					
					// Add the client to the clientsToKick list
					clientsToKick.add(client);
				}
			}
		}
		
		// Kick the clients
		for (ClientEndpointHandler clientEndpointHandler : clientsToKick) {
			kickClient(clientEndpointHandler);
		}
	}
	
	/*
	 * Kicks a client
	 * */
	public void kickClient(ClientEndpointHandler client){
		client.disconnectFromEndpoint();
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
	

	@Override
	public void onEndpointConnected(EndpointHandler handler) {
		// Print a log message
		System.out.println("Connection from " + handler.getEndpointIp());
	}

	@Override
	public void onEndpointDisconnected(EndpointHandler handler) {
		// Print a log message 
		System.out.println("Client with IP " + handler.getEndpointIp() + " disconnected");
		
		// Remove the disconnected client
		if(mIsListening){
			mClientConnections.remove(handler);
		}
		
		if (ClientEndpointHandler.class.isInstance(handler)){
			ClientEndpointHandler client = (ClientEndpointHandler) handler;

			Map<String, String> parameters = new HashMap<String, String>();
			parameters.put("username", client.getUsername());	
			
			distributePacket("leave", parameters, handler);
		}
	}

	@Override
	public void onDataReceived(EndpointHandler handler, String rawData) {
		
	}

	
	private boolean isAddressBanned(byte[] address){
		boolean isClientBanned = false;
		byte[] clientAddress = address;
		
		for (byte[] bannedIp : mBannedIps) {
			boolean isEqualAddress = true;
			for (int i = 0; i < bannedIp.length; i++) {
				isEqualAddress = isEqualAddress && clientAddress[i] == bannedIp[i];
			}
			
			if(isEqualAddress){
				isClientBanned = true;
			}
		}
		return isClientBanned;
	}
}
