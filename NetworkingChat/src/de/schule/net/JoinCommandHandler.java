package de.schule.net;

import java.util.Map;

import com.google.gson.JsonObject;

public class JoinCommandHandler implements CommandHandler{
	private ChatClient mClient;
	
	public JoinCommandHandler(ChatClient client){
		mClient = client;
	}
	
	@Override
	public String getCommandName() {
		return "join";
	}

	@Override
	public Map<String, String> processCommand(JsonObject jsonObject, EndpointHandler endpoint) {

		// Get the username
		String username = jsonObject.get("username").getAsString();

		mClient.onUserJoined(username);
	
		return null;
	}
}
