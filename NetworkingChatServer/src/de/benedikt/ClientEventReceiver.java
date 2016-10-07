package de.benedikt;

public interface ClientEventReceiver {
	void OnMessageReceived(ChatMessage message, ChatClientHandler connection);
	void OnClientDisconnected(ChatClientHandler connection);
}
