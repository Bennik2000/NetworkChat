package de.schule.net;

/*
 * Derived EndpointHandler class which contains more information about the client
 * */
public class ClientEndpointHandler extends EndpointHandler {
	private String mUsername;
	private ChatServer mServer;
	
	public ClientEndpointHandler(PacketHandler packetHandler) {
		super(packetHandler);
	}
	
	public ClientEndpointHandler(PacketHandler packetHandler, ChatServer server) {
		super(packetHandler);
		setServer(server);
	}

	public String getUsername() {
		return mUsername;
	}

	public void setUsername(String mUsername) {
		this.mUsername = mUsername;
	}

	public ChatServer getServer() {
		return mServer;
	}

	public void setServer(ChatServer mServer) {
		this.mServer = mServer;
	}
}
