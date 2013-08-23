package com.fred;

import java.io.File;
import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;

public class AudioFileTestPanel extends GridPane {

	@FXML
	TextField txtPath;
	
	public AudioFileTestPanel() {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("audio_file_test_panel.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);

		try {
			fxmlLoader.load();
		}
		catch (IOException exception) {
			throw new RuntimeException(exception);
		}
	}
	
	@FXML
	protected void handleBrowseButtonAction(ActionEvent event) {
		
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Resource File");
		File file = fileChooser.showOpenDialog(this.getScene().getWindow());
		if (file != null) {
             openAudioFile(file);
		}
		
	}
	
	private void openAudioFile(File file){
		 String path = file.getPath();
		 txtPath.setText(path);
		 
	}
	
}
