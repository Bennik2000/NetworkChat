package de.schule.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatClientHandler implements Runnable {
	private Socket mSocket;
	private BufferedReader mBufferedReader;
	private PrintWriter mPrintWriter;
	private String mClientIp;
	private Thread mHandlerThread;
	private ChatServer mChatServer;

	private List<ClientEventReceiver> mEventReceivers;

	private boolean mConnected;

	public ChatClientHandler(String username, ChatServer server) {
		mEventReceivers = new ArrayList<>();
		mChatServer = server;
	}

	public void setupConnection(Socket socket) throws IOException {
		mSocket = socket;
		mClientIp = mSocket.getRemoteSocketAddress().toString();
		mBufferedReader = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
		mPrintWriter = new PrintWriter(new OutputStreamWriter(mSocket.getOutputStream()));

		mConnected = true;
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

		if (mPrintWriter != null) {
			try {
				mPrintWriter.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

			mPrintWriter = null;
		}

		if (mHandlerThread != null) {
			if (!mHandlerThread.isInterrupted()) {
				mHandlerThread.interrupt();
			}

			mHandlerThread = null;
		}
	}

	public void startListeningForMessages() {
		mHandlerThread = new Thread(this);
		mHandlerThread.start();
	}

	public void sendMessage(ChatMessage message) {
		sendMessage(message.getMessage());
	}

	private void sendMessage(String string) {
		if (!mConnected)
			return;

		mPrintWriter.println(string);
		mPrintWriter.flush();
	}

	private void onMessageReceived(ChatMessage message) {
		for (ClientEventReceiver clientEventReceiver : mEventReceivers) {
			clientEventReceiver.OnMessageReceived(message, this);
		}
	}

	private void onClientDisconnected() {
		for (ClientEventReceiver clientEventReceiver : mEventReceivers) {
			clientEventReceiver.OnClientDisconnected(this);
		}
	}

	@Override
	public void run() {
		try {
			String receivedLine = null;

			while ((receivedLine = mBufferedReader.readLine()) != null && mConnected) {
				processMessage(receivedLine);
			}

			closeConnection();

		} catch (IOException e) {
			closeConnection();
		}

		onClientDisconnected();
	}
	
	private void processMessage(String message){
		String formatted = message.trim();
		
		if(formatted.startsWith("/")){
			switch(formatted.substring(1, formatted.length())){
			case "members":
				
				sendMessage(String.valueOf(mChatServer.getMemberCount()));
				
				break;
			}
		}
		else{
			ChatMessage chatMessage = new ChatMessage();
			chatMessage.setMessage(message);

			onMessageReceived(chatMessage);
		}
	}

	public void registerEventReceiver(ClientEventReceiver receiver) {
		mEventReceivers.add(receiver);
	}

	public void unregisterEventReceiver(ClientEventReceiver receiver) {
		mEventReceivers.remove(receiver);
	}

	public String getClientIp() {
		return mClientIp;
	}
}
