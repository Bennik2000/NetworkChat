package de.schule.net;

import java.util.HashMap;
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


		if (ClientEndpointHandler.class.isInstance(endpoint)){
			ClientEndpointHandler client = (ClientEndpointHandler) endpoint;
			

			Map<String, String> parameters = new HashMap<String, String>();
			parameters.put("message", message);
			parameters.put("username", client.getUsername());
			
			client.getServer().distributePacket("message", parameters, endpoint);
			
			System.out.println("Message from " + client.getUsername() + ": " + message);
		}
		
		return null;
	}
}
