package de.schule.net;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatClient implements EndpointEventReceiver{
	private List<ClientEventReceiver> mClientEventReceivers;
	private EndpointHandler mEndpointHandler;
	private JsonPacketHandler mPacketHandler; 
	
	public ChatClient(){
		mPacketHandler = new JsonPacketHandler();
		mPacketHandler.registerCommandHandler(new MessageCommandHandler());
		
		mClientEventReceivers = new ArrayList<>();
	}
	
	
	public void connectToServer(String ip, int port) throws UnknownHostException, IOException{
		// Connect to the server
		Socket socket = new Socket(ip, port);
		
		// Create a new EndpointHandler
		mEndpointHandler = new EndpointHandler(mPacketHandler);
		
		// Setup the EndpointHandler
		mEndpointHandler.registerEventReceiver(this);
		mEndpointHandler.setupEndpointConnection(socket);
	}
	
	public void disconnectFromServer(){
		mEndpointHandler.disconnectFromEndpoint();
	}
	
	
	public void sendMessage(String message){
		
		// Create the parameters
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("message", message);
		
		// Send the packet
		mEndpointHandler.sendPacket("message", parameters);
	}

	
	public String getServerIp(){
		return mEndpointHandler.getEndpointIp();
	}

	public boolean isConnected(){
		return mEndpointHandler.isConnected();
	}
	
	public void setUsername(String username){
		
		// Create the parameters
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("username", username);
		
		// Send the packet
		mEndpointHandler.sendPacket("username", parameters);
	}
	
	
	public void registerClientEventReceiver(ClientEventReceiver clientEventReceiver){
		mClientEventReceivers.add(clientEventReceiver);
	}
	
	public void unregisterClientEventReceiver(ClientEventReceiver clientEventReceiver){
		mClientEventReceivers.remove(clientEventReceiver);
	}

	
	@Override
	public void onEndpointConnected(EndpointHandler handler) {
		for (ClientEventReceiver clientEventReceiver : mClientEventReceivers) {
			clientEventReceiver.onConnected(this);
		}
	}

	@Override
	public void onEndpointDisconnected(EndpointHandler handler) {
		for (ClientEventReceiver clientEventReceiver : mClientEventReceivers) {
			clientEventReceiver.onDisconnected(this);
		}
	}

	@Override
	public void onDataReceived(EndpointHandler handler, String rawData) {
		
	}
}
