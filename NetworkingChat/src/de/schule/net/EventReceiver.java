package de.schule.net;

public interface EventReceiver {
	
	/*
	 * Gets called, when a message was received
	 * */
	void OnMessageReceived(ChatMessage message);
	
	/*
	 * Gets called, when the client connected to the server
	 * */
	void OnConnected(String ip);
	
	/*
	 * Gets called, when the client was disconnected
	 * */
	void OnDisconnected();
}
