package de.schule.net;

public interface ClientEventReceiver {
	
	/*
	 * Gets called, when a message was received
	 * */
	void OnMessageReceived(ChatMessage message, ChatClientHandler connection);
	
	/*
	 * Gets called, when a client disconnected
	 * */
	void OnClientDisconnected(ChatClientHandler connection);
}
