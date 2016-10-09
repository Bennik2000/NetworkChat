package de.schule.net;

import java.util.HashMap;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/*
 * JSON implementation for the PacketHandler
 * */
public class JsonPacketHandler implements PacketHandler{
	private Map<String, CommandHandler> mRegisteredCommands;

	public JsonPacketHandler() {
		mRegisteredCommands = new HashMap<>();
	}
	
	
	@Override
	public String processPacket(String rawPacket, EndpointHandler endpoint) {
		Gson gson = new Gson();
		
		// Parse the packet
		JsonObject jsonObject = gson.fromJson(rawPacket, JsonObject.class);
		
		// Get the command attribute
		JsonElement commandElement = jsonObject.get("command");
		String command = commandElement.getAsString();
		
		// When the command is available we handle it
		if(mRegisteredCommands.containsKey(command)){
			CommandHandler handler = mRegisteredCommands.get(command);
			
			// Process the command
			Map<String, String> response = handler.processCommand(jsonObject, endpoint);
			
			// When a response packet is available we send return response
			if(response != null){
				return constructPacket("response", response, endpoint);
			}
		}
		
		return null;
	}

	@Override
	public String constructPacket(String command, Map<String, String> parameters, EndpointHandler endpoint) {
		
		JsonObject innerObject = new JsonObject();
		
		// Add the command attribute 
		innerObject.addProperty("command", command);

		// Add all parameters
		for (String key : parameters.keySet()) {
			innerObject.addProperty(key, parameters.get(key));
		}
		
		// Create the json string
		Gson gson = new GsonBuilder().create();
		return gson.toJson(innerObject);
	}

	
	public void registerCommandHandler(CommandHandler handler){
		if(handler == null) return;
		
		if(!mRegisteredCommands.containsKey(handler.getCommandName())){
			mRegisteredCommands.put(handler.getCommandName(), handler);
		}
	}
	
	public void unregisterCommandHandler(CommandHandler handler){
		if(handler == null) return;
		
		if(mRegisteredCommands.containsKey(handler.getCommandName())){
			mRegisteredCommands.remove(handler.getCommandName());
		}
	}
}
