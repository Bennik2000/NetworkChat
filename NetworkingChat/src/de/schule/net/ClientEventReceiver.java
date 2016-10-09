package de.schule.net;

/*
 * Event receiver for the ChatClient
 * */
public interface ClientEventReceiver {
	void onUserJoined(ChatClient client, String user);
	void onUserLeft(ChatClient client, String user);
	void onMessageReceived(ChatClient client, String message, String user);
	void onConnected(ChatClient client);
	void onDisconnected(ChatClient client);
}
