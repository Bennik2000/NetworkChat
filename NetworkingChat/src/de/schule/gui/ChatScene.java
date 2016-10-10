package de.schule.gui;

import de.schule.net.ChatClient;
import de.schule.net.ClientEventReceiver;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ChatScene extends BorderPane implements ClientEventReceiver{
	private Stage mStage;
	private Button mSendButton;
	private TextField mTextField;
	private ListView<String> mChatHistory;
	private ChatClient mClient;

	public ChatScene(Stage stage, ChatClient client) {
		mClient = client;
		mStage = stage;
		initializeScene();
		
		mStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			
			@Override
			public void handle(WindowEvent event) {
				mClient.disconnectFromServer();
			}
		});
	}

	
	private void initializeScene(){
		mChatHistory = new ListView<>();
		mSendButton = new Button(">");
		mTextField = new TextField();
		
		mSendButton.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				sendMessage();
			}
		});
		

		HBox sendMessageHBox = new HBox();
		sendMessageHBox.getChildren().add(mTextField);
		sendMessageHBox.getChildren().add(mSendButton);


		setWidth(mStage.getWidth());

		setCenter(mChatHistory);
		setBottom(sendMessageHBox);
	}

	private void sendMessage(){
		String message = mTextField.getText();
		mTextField.setText("");
		
		mClient.sendMessage(message);
		
		mChatHistory.getItems().add("Du: " + message);
	}

	@Override
	public void onUserJoined(ChatClient client, String user) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				mChatHistory.getItems().add(user + " joined the chatroom");
			}
		});
	}


	@Override
	public void onUserLeft(ChatClient client, String user) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				mChatHistory.getItems().add(user + " left the chatroom");
			}
		});
	}


	@Override
	public void onMessageReceived(ChatClient client, String message, String user) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				mChatHistory.getItems().add(user + ": " + message);
			}
		});
	}


	@Override
	public void onConnected(ChatClient client) {
		
	}

	@Override
	public void onDisconnected(ChatClient client) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				mStage.close();
			}
		});
	}
}
