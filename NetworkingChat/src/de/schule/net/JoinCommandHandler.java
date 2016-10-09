package de.schule.net;

import java.util.Map;

import com.google.gson.JsonObject;

public class JoinCommandHandler implements CommandHandler{

	@Override
	public String getCommandName() {
		return "join";
	}

	@Override
	public Map<String, String> processCommand(JsonObject jsonObject, EndpointHandler endpoint) {

		// Get the username
		String username = jsonObject.get("username").getAsString();

		// Print the message
		System.out.println(username + " connected");
	
		return null;
	}
}
