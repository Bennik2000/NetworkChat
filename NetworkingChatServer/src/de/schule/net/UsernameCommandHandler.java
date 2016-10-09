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
		String username = jsonObject.get("username").getAsString();


		if (ClientEndpointHandler.class.isInstance(endpoint)){
			ClientEndpointHandler client = (ClientEndpointHandler) endpoint;
			
			client.setUsername(username);
			
			System.out.println(endpoint.getEndpointIp() + " identifies as " + client.getUsername());
		}
		
		return null;
	}

}
