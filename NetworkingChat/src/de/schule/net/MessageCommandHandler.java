package de.schule.net;

import java.util.Map;

import com.google.gson.JsonObject;

public class MessageCommandHandler implements CommandHandler {

	@Override
	public String getCommandName() {
		return "message";
	}

	@Override
	public Map<String, String> processCommand(JsonObject jsonObject, EndpointHandler endpoint) {
		String message = jsonObject.get("message").getAsString();
		String username = jsonObject.get("username").getAsString();

		System.out.println(username + ": " + message);
	
		return null;
	}
}
