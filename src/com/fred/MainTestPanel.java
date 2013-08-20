package com.fred;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class MainTestPanel extends Application {

    public static void main(String[] args) {
        launch(args);
    }
    
    
	@Override
	public void start(Stage stage) throws Exception {
		GridPane root = (GridPane)FXMLLoader.load(getClass().getResource("main_test_panel.fxml"));
		
		Scene scene = new Scene(root, 1000, 700);

		stage.setTitle("FFT testing");
		stage.setScene(scene);
		stage.show();

	}

}
