package de.benedikt;

public class ChatMessage {
	private String mMessage;
	private String mUsername;
	
	
	public String getUsername() {
		return mUsername;
	}
	public void setUsername(String username) {
		this.mUsername = username;
	}
	
	public String getMessage() {
		return mMessage;
	}
	public void setMessage(String message) {
		this.mMessage = message;
	}
}
