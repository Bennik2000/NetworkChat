package de.schule.net;

import java.util.Map;

import com.google.gson.JsonObject;

public class UsernameCommandHandler implements CommandHandler {

	@Override
	public String getCommandName() {
		return "username";
	}

	@Override
	public Map<String, String> processCommand(JsonObject jsonObject, EndpointHandler endpoint) {
		
		// Read the username from the json object
		String username = jsonObject.get("username").getAsString();


		// When the end point is a ClientEndpointHandler we can go on
		if (ClientEndpointHandler.class.isInstance(endpoint)){
			ClientEndpointHandler client = (ClientEndpointHandler) endpoint;
			
			// Set the username
			client.setUsername(username);
			
			// Print a log message
			System.out.println(endpoint.getEndpointIp() + " identifies as " + client.getUsername());
		}
		
		return null;
	}
}
