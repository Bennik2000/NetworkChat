package de.schule.net;

public interface ClientEventReceiver {
	void onConnected(ChatClient client);
	void onDisconnected(ChatClient client);
}
