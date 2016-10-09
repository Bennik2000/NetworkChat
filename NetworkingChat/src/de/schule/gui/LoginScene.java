package de.schule.gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import javafx.stage.Stage;


public class LoginScene extends BorderPane {
	private TextField mUsernameTextField;
	private TextField mIpTextField;
	private Stage mStage;
	
	public LoginScene(Stage stage) {
		mStage = stage;
		initializeScene();
	}

	private void initializeScene(){
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		
		// The server IP text and text field
		Text serverIp = new Text("Server Ip:");
		grid.add(serverIp, 0, 0); 

		mUsernameTextField = new TextField("localhost");
	    grid.add(mUsernameTextField, 1, 0);
	    
	    // The username text and text field
		Text username = new Text("Username:");
		grid.add(username, 0, 1);

		mIpTextField = new TextField();
	    grid.add(mIpTextField, 1, 1);
	    

	    // The connect button
	    Button connectButton = new Button("Connect");
	    connectButton.setMaxWidth(Double.MAX_VALUE);
	    connectButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				onConnectClick();
			}
		});
	    
	    
	    HBox buttonBox = new HBox();
	    HBox.setHgrow(connectButton, Priority.ALWAYS);
	    buttonBox.getChildren().add(connectButton);
	    
	    grid.add(buttonBox, 0, 2, 2, 1);


	    setCenter(grid);
	}
	
	private void onConnectClick(){
		mStage.close();
	}
}
