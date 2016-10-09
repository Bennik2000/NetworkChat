package de.schule.net;

import java.util.Map;

import com.google.gson.JsonObject;

public class LeaveCommandHandler implements CommandHandler{

	@Override
	public String getCommandName() {
		return "leave";
	}

	@Override
	public Map<String, String> processCommand(JsonObject jsonObject, EndpointHandler endpoint) {

		// Get the username
		String username = jsonObject.get("username").getAsString();

		// Print the message
		System.out.println(username + " disconnected");
	
		return null;
	}
}
