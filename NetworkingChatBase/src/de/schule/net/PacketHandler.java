package de.schule.net;

import java.util.Map;

/*
 * The PacketHandler handles a raw received packet
 * */
public interface PacketHandler {
	String processPacket(String rawPacket, EndpointHandler endpoint);
	String constructPacket(String command, Map<String, String> parameters, EndpointHandler endpoint);
}
