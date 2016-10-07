package de.schule.net;

public interface EventReceiver {
	void OnMessageReceived(ChatMessage message);
	void OnConnected(String ip);
	void OnDisconnected();
}
