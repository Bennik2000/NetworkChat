package de.schule.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EndpointHandler implements Runnable{
	private String mEndpointIP;
	private boolean mConnected;
	
	private Socket mEndpointSocket;
	private BufferedReader mBufferedReader;
	private BufferedWriter mBufferedWriter;
	private PacketHandler mPacketHandler;
	private Thread mHandlerThread;
	
	public List<EndpointEventReceiver> mEventReceivers;
	
	
	public EndpointHandler(PacketHandler packetHandler){
		mPacketHandler = packetHandler;
		mEventReceivers = new ArrayList<>();
	}
	
	public void setupEndpointConnection(Socket socket) throws IOException{
		mEndpointSocket = socket;
		mEndpointIP = mEndpointSocket.getRemoteSocketAddress().toString();
		
		// Grab the I/O streams
		mBufferedReader = new BufferedReader(new InputStreamReader(mEndpointSocket.getInputStream(), "UTF-8"));
		mBufferedWriter = new BufferedWriter(new OutputStreamWriter(mEndpointSocket.getOutputStream(), "UTF-8"));

		mConnected = true;
		
		mHandlerThread = new Thread(this);
		mHandlerThread.start();
		
		onClientConnected();
	}
	
	public void disconnectFromEndpoint(){
		if (!mConnected)
			return;

		mConnected = false;

		// Try to close the socket 
		if (mEndpointSocket != null) {
			try {
				mEndpointSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			mEndpointSocket = null;
		}

		// Try to close the buffered reader
		if (mBufferedReader != null) {
			try {
				mBufferedReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			mBufferedReader = null;
		}

		// Try to close the buffered writer
		if (mBufferedWriter != null) {
			try {
				mBufferedWriter.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

			mBufferedWriter = null;
		}

		// Try to interrupt the handler thread
		if (mHandlerThread != null) {
			if (!mHandlerThread.isInterrupted()) {
				mHandlerThread.interrupt();
			}

			mHandlerThread = null;
		}
	}

	
	@Override
	public void run() {
		listenForIncomingData();
	}

	
	public void registerEventReceiver(EndpointEventReceiver receiver) {
		mEventReceivers.add(receiver);
	}

	public void unregisterEventReceiver(EndpointEventReceiver receiver) {
		mEventReceivers.remove(receiver);
	}

	
	public String getEndpointIp(){
		return mEndpointIP;
	}

	public boolean isConnected(){
		return mConnected;
	}
	
	private void listenForIncomingData(){
		try {			
			String receivedLine = null;

			// Read all lines which come in
			while ((receivedLine = mBufferedReader.readLine()) != null && mConnected) {
				
				// Process the received line
				if(mPacketHandler != null){
					try{
						String response = mPacketHandler.processPacket(receivedLine, this);
						
						// When the packetHandler returned a response packet we send it
						if(response != null){
							sendPacket(response);
						}
					}
					catch(Exception e){
						System.out.println("Internal server error while processing the packet:");
						e.printStackTrace();
					}
				}
			}

			// When the loop finishes we can close the connection
			disconnectFromEndpoint();

		} catch (IOException e) {
			
			// When an exception was thrown we close the connection
			disconnectFromEndpoint();
		}

		// Notify that the client was disconnected
		onClientDisconnected();
	}
	

	public void sendPacket(String command, Map<String, String> parameters){
		// Construct the packet using the packet handler
		
		try{
			String packet = mPacketHandler.constructPacket(command, parameters, this);
			
			sendPacket(packet);
		}
		catch (Exception e) {
			System.out.println("Error while sending packet:");
			e.printStackTrace();
		}
	}
	
	private void sendPacket(String packet){
		if(!mConnected) return;
		
		try {
			// Send the packet
			mBufferedWriter.write(packet);
			mBufferedWriter.newLine();
			mBufferedWriter.flush();
		} catch (IOException e) {
			
			// When an exception was thrown we close the connection
			disconnectFromEndpoint();
		}
	}
	
	

	private void onClientDisconnected() {
		for (EndpointEventReceiver clientEventReceiver : mEventReceivers) {
			clientEventReceiver.onEndpointDisconnected(this);
		}
	}

	private void onClientConnected() {
		for (EndpointEventReceiver clientEventReceiver : mEventReceivers) {
			clientEventReceiver.onEndpointConnected(this);
		}
	}
}
