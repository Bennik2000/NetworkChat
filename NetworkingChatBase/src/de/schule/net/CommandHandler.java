package de.schule.net;

import java.util.Map;

import com.google.gson.JsonObject;

public interface CommandHandler {
	public String getCommandName();
	public Map<String, String> processCommand(JsonObject jsonObject, EndpointHandler endpoint);
}
