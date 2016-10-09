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

		// Read the message from the json object
		String message = jsonObject.get("message").getAsString();


		// When the end point is a ClientEndpointHandler we can go on
		if (ClientEndpointHandler.class.isInstance(endpoint)){
			ClientEndpointHandler client = (ClientEndpointHandler) endpoint;
			
			// Set the parameters for the distributed packet
			Map<String, String> parameters = new HashMap<String, String>();
			parameters.put("message", message);
			parameters.put("username", client.getUsername());
			
			// Distribute the packet to the other clients
			client.getServer().distributePacket("message", parameters, endpoint);
			
			// Print a log message
			System.out.println("Message from " + client.getUsername() + ": " + message);
		}
		
		return null;
	}
}
