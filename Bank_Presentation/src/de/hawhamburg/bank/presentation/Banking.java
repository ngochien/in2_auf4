package de.hawhamburg.bank.presentation;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import de.hawhamburg.bank.services.ApplicationContext;

public class Banking extends Application {
	private static final String PERSISTENCE_UNIT = "haw_bank";

	public static void main(final String[] args) {
		launch(args);
	}

	private Parent root;

	@Override
	public void init() throws Exception {
		ApplicationContext.getApplicationContext().initJPA(PERSISTENCE_UNIT);
		root = FXMLLoader.load(Banking.class.getResource("banking.fxml"));
	}

	@Override
	public void start(final Stage primaryStage) throws Exception {
		primaryStage.setTitle("HAW Banking");
		Scene myScene = new Scene(root);
		primaryStage.setScene(myScene);
		primaryStage.show();
	}
}