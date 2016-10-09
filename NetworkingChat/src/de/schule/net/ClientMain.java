package de.schule.net;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ClientMain implements Runnable, ClientEventReceiver {
	public static void main(String[] args) {
		mBufferedReader = new BufferedReader(new InputStreamReader(System.in));
		mChatClient = new ChatClient();
		
		System.out.print("Ender your username: ");
		
		String username;
		try {
			username = mBufferedReader.readLine();
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}
		
		ClientMain mClient = new ClientMain(username);
		
		mClient.join();
		
		try {
			mBufferedReader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private static BufferedReader mBufferedReader;
	private static ChatClient mChatClient;
	private static Thread mThread;
	
	public ClientMain(String username){
		mThread = new Thread(this);
		mChatClient.registerClientEventReceiver(this);
		
		try {
			mChatClient.connectToServer("localhost", 25552);
			
		} catch (IOException e){ 
			e.printStackTrace();
			return;
		}
		
		mChatClient.setUsername(username);
	}

	@Override
	public void onConnected(ChatClient client) {
		System.out.println("Connected to server at " + client.getServerIp());
		
		mThread.start();
	}

	@Override
	public void onDisconnected(ChatClient client) {
		System.out.println("Disconnected from server");

		mThread.interrupt();
	}

	@Override
	public void run() {
		String line;
		
		try {
			
			while((line = mBufferedReader.readLine()) != null){
				mChatClient.sendMessage(line);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("Scanner closed");
	}
	
	public void join(){
		try {
			mThread.join();
		} catch (InterruptedException e) { }
	}
}
