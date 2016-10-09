package de.schule.net;

/*
 * Event receiver for the ChatClient
 * */
public interface ClientEventReceiver {
	void onConnected(ChatClient client);
	void onDisconnected(ChatClient client);
}
