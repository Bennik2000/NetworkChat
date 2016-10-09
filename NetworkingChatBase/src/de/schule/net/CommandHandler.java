package de.schule.net;

import java.util.Map;

import com.google.gson.JsonObject;

/*
 * A CommandHandler processes a received command
 * */
public interface CommandHandler {
	public String getCommandName();
	public Map<String, String> processCommand(JsonObject jsonObject, EndpointHandler endpoint);
}
