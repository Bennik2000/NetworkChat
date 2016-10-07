package de.schule.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.*;

public class ChatClientHandler implements Runnable {
	
	/*
	 * Contains the socket connected to the client
	 * */
	private Socket mSocket;
	
	/*
	 * BufferedReader connected to the client
	 * */
	private BufferedReader mBufferedReader;
	

	/*
	 * BufferedReader connected to the client
	 * */
	private BufferedWriter mBufferedWriter;
	
	/*
	 * The IP address of the client
	 * */
	private String mClientIp;
	
	/*
	 * The user name of the client
	 * */
	private String mUsername;
	
	/*
	 * The thread which handles the client
	 * */
	private Thread mHandlerThread;
	
	/*
	 * The ChatServer instance
	 * */
	private ChatServer mChatServer;

	/*
	 * Contains all registered event receivers
	 * */
	private List<ClientEventReceiver> mEventReceivers;

	/*
	 * Indicates whether the client is connected
	 * */
	private boolean mConnected;

	public ChatClientHandler(String username, ChatServer server) {
		mEventReceivers = new ArrayList<>();
		mChatServer = server;
	}

	/*
	 * Setup the connection
	 * */
	public void setupConnection(Socket socket) throws IOException {
		mSocket = socket;
		mClientIp = mSocket.getRemoteSocketAddress().toString();
		
		// Grab the I/O streams
		mBufferedReader = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
		mBufferedWriter = new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream()));

		mConnected = true;
	}

	/*
	 * Close the connection
	 * */
	public void closeConnection() {
		if (!mConnected)
			return;

		mConnected = false;

		// Try to close the socket 
		if (mSocket != null) {
			try {
				mSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			mSocket = null;
		}

		// Try to close the buffered reader
		if (mBufferedReader != null) {
			try {
				mBufferedReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			mBufferedReader = null;
		}

		// Try to close the buffered writer
		if (mBufferedWriter != null) {
			try {
				mBufferedWriter.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

			mBufferedWriter = null;
		}

		// Try to interrupt the handler thread
		if (mHandlerThread != null) {
			if (!mHandlerThread.isInterrupted()) {
				mHandlerThread.interrupt();
			}

			mHandlerThread = null;
		}
	}

	/*
	 * Starts the listening for incoming messages
	 * */
	public void startListeningForMessages() {
		mHandlerThread = new Thread(this);
		mHandlerThread.start();
	}

	/*
	 * Sends a message to the client
	 * */
	public void sendMessage(ChatMessage message) {
		Map<String, String> properties = new HashMap<String, String>();
		properties.put("message", message.getMessage());
		properties.put("username", message.getUsername());
		
		sendMessage("message", properties);
	}

	public void sendMessage(String command, Map<String, String> parameters){
		JsonObject innerObject = new JsonObject();
		innerObject.addProperty("command", command);

		for (String key : parameters.keySet()) {
			innerObject.addProperty(key, parameters.get(key));
		}
		
		sendMessage(new Gson().toJson(innerObject));
	}
	
	/*
	 * Sends a string to the client
	 * */
	private void sendMessage(String string) {
		if (!mConnected)
			return;

		try {
			// Send a string without newline
			mBufferedWriter.write(string.replace("\n", ""));
			
			// Send a newline
			mBufferedWriter.write("\n");
			
			// Flush the writer to ensure that the message was sent
			mBufferedWriter.flush();
		} catch (IOException e) { }
	}

	private void onMessageReceived(ChatMessage message) {
		for (ClientEventReceiver clientEventReceiver : mEventReceivers) {
			clientEventReceiver.OnMessageReceived(message, this);
		}
	}

	private void onClientDisconnected() {
		for (ClientEventReceiver clientEventReceiver : mEventReceivers) {
			clientEventReceiver.OnClientDisconnected(this);
		}
	}

	@Override
	public void run() {
		try {			
			String receivedLine = null;

			// Read all lines which come in
			while ((receivedLine = mBufferedReader.readLine()) != null && mConnected) {
				
				// Process the received line
				processMessage(receivedLine);
			}

			// When the loop finishes we can close the connection
			closeConnection();

		} catch (IOException e) {
			
			// When an exception was thrown we close the connection
			closeConnection();
		}

		// Notify that the client was disconnected
		onClientDisconnected();
	}
	
	/*
	 * Processes messages from the client
	 * */
	private void processMessage(String message){
		
		Gson gson = new Gson();
		
		JsonObject jsonObject = gson.fromJson(message, JsonObject.class);
		JsonElement commandElement = jsonObject.get("command");
		
		String command = commandElement.getAsString();
		
		System.out.println(message);
		
		switch (command) {
		case "message":
			JsonElement messageElement = jsonObject.get("message");
		
			ChatMessage chatMessage = new ChatMessage();
			
			chatMessage.setMessage(messageElement.getAsString());
			chatMessage.setUsername(mUsername);
			
			onMessageReceived(chatMessage);
			
			break;

		case "username":
			JsonElement nameElement = jsonObject.get("name");
			mUsername = nameElement.getAsString(); 
			
			break;
			
		default:
			break;
		}
		
		//String formatted = message.trim();
		//
		//// Check if the message is a command
		//if(formatted.startsWith("/")){
		//	
		//	// Select the action based on the command
		//	switch(formatted.substring(1, formatted.length())){
		//		case "members":
		//			sendMessage(String.valueOf(mChatServer.getClientCount()));
		//			break;
		//		case "ping":
		//			sendMessage("pong!");
		//			break;
		//		}
		//}
		//else{
		//	
		//	// Route the message to the server
		//	ChatMessage chatMessage = new ChatMessage();
		//	chatMessage.setMessage(message);
        //
		//	onMessageReceived(chatMessage);
		//}
	}

	public void registerEventReceiver(ClientEventReceiver receiver) {
		mEventReceivers.add(receiver);
	}

	public void unregisterEventReceiver(ClientEventReceiver receiver) {
		mEventReceivers.remove(receiver);
	}

	public String getClientIp() {
		return mClientIp;
	}
}
