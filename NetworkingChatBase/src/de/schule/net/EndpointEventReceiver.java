package de.schule.net;

/*
 * Event receiver for the EndpointHandler 
 * */
public interface EndpointEventReceiver {
	void onEndpointConnected(EndpointHandler handler);
	void onEndpointDisconnected(EndpointHandler handler);
	void onDataReceived(EndpointHandler handler, String rawData);
}
