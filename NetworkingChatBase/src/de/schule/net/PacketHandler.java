package de.schule.net;

import java.util.Map;

public interface PacketHandler {
	String processPacket(String rawPacket, EndpointHandler endpoint);
	String constructPacket(String command, Map<String, String> parameters, EndpointHandler endpoint);
}
