package de.schule.net;

public interface EndpointEventReceiver {
	void onEndpointConnected(EndpointHandler handler);
	void onEndpointDisconnected(EndpointHandler handler);
	void onDataReceived(EndpointHandler handler, String rawData);
}
