package de.benedikt;

public interface EventReceiver {
	void OnMessageReceived(ChatMessage message);
	void OnConnected(String ip);
	void OnDisconnected();
}
