package de.schule.net;

import java.util.Map;

import com.google.gson.JsonObject;

public class MessageCommandHandler implements CommandHandler {
	private ChatClient mClient;
	
	public MessageCommandHandler(ChatClient client){
		mClient = client;
	}
	
	@Override
	public String getCommandName() {
		return "message";
	}

	@Override
	public Map<String, String> processCommand(JsonObject jsonObject, EndpointHandler endpoint) {
		
		// Get the message
		String message = jsonObject.get("message").getAsString();
		
		// Get the username
		String username = jsonObject.get("username").getAsString();

		mClient.onMessageReceived(message, username);
		
		return null;
	}
}
