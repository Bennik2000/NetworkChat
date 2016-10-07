package de.schule.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class ChatClient implements Runnable {
	private static final int mPort = 25552;

	private String mIp;
	private Socket mSocket;
	private BufferedWriter mBufferedWriter;
	private BufferedReader mBufferedReader;
	private Thread mHandlerThread;
	
	
	private boolean mConnected;
	
	private List<EventReceiver> mMessageReceivers;
	
	public ChatClient(String ip){
		mIp = ip;
		mMessageReceivers = new ArrayList<>();
	}
	
	public void connectToServer(){
		try {
			mSocket = new Socket(mIp, mPort);

			mBufferedWriter = new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream()));
			mBufferedReader = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));

			
			mConnected = true;

			mHandlerThread  = new Thread(this);
			mHandlerThread.start();
			

			onConnected();
		}
		catch (UnknownHostException e) { } 
		catch (IOException e) { }
	}
	
	public void closeConnection() {
		if (!mConnected)
			return;

		mConnected = false;

		if (mSocket != null) {
			try {
				mSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			mSocket = null;
		}

		if (mBufferedReader != null) {
			try {
				mBufferedReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			mBufferedReader = null;
		}

		if (mBufferedWriter != null) {
			try {
				mBufferedWriter.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

			mBufferedWriter = null;
		}

		if (mHandlerThread != null) {
			if (!mHandlerThread.isInterrupted()) {
				mHandlerThread.interrupt();
			}

			mHandlerThread = null;
		}
	}
	
	@Override
	public void run() {
		try {
			String receivedLine = null;

			while ((receivedLine = mBufferedReader.readLine()) != null && mConnected) {
				ChatMessage message = new ChatMessage();
				message.setMessage(receivedLine);

				onMessageReceived(message);
			}

			closeConnection();

		} catch (IOException e) {
			closeConnection();
		}

		onDisconnected();
	}

	public void sendMessage(ChatMessage message){
		sendMessage(message.getMessage());
	}
	
	private void sendMessage(String message){
		if (!mConnected)
			return;

		
		try {
			mBufferedWriter.write(message.replace("\n", ""));
			mBufferedWriter.write("\n");
			mBufferedWriter.flush();
		} catch (IOException e) {
		}
	}
	
	
	public void registerEventReceiver(EventReceiver messageReceiver){
		mMessageReceivers.add(messageReceiver);
	}
	public void unregisterEventReceiver(EventReceiver messageReceiver){
		mMessageReceivers.remove(messageReceiver);
	}
	
	private void onMessageReceived(ChatMessage message){
		for (EventReceiver eventReceiver : mMessageReceivers) {
			eventReceiver.OnMessageReceived(message);
		}
	}
	
	private void onDisconnected(){
		for (EventReceiver eventReceiver : mMessageReceivers) {
			eventReceiver.OnDisconnected();
		}
	}
	
	private void onConnected(){
		for (EventReceiver eventReceiver : mMessageReceivers) {
			eventReceiver.OnConnected(mIp);
		}
	}
}
