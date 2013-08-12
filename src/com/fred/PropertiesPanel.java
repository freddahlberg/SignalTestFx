package com.fred;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class PropertiesPanel extends HBox {

	@FXML
	private CheckBox chkActive;
	@FXML
	private TextField txtFrequency;
	@FXML
	private TextField txtWaveForm;
	@FXML
	private TextField txtAmplitude;

	public PropertiesPanel() {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("properties_panel.fxml"));
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
	protected void doSomething() {
		System.out.println("The button was clicked!");
	}

	public SignalComponentProperties getSignalComponentProperties() {

		SignalComponentProperties props = new SignalComponentProperties();
		props.setActive(chkActive.isSelected());
		try {
			props.setFrequency(Double.parseDouble(txtFrequency.getText().trim()));
			props.setWaveForm(Integer.parseInt(txtWaveForm.getText().trim()));
			props.setAmplitude(Double.parseDouble(txtAmplitude.getText().trim()));
		}
		catch (NumberFormatException e) {
			props.setActive(false);
		}

		return props;
	}

	public void setSignalComponentProperties(SignalComponentProperties props) {
		chkActive.setSelected(props.isActive());
		txtFrequency.setText(String.valueOf(props.getFrequency()));
		txtWaveForm.setText(String.valueOf(props.getWaveForm()));
		txtAmplitude.setText(String.valueOf(props.getAmplitude()));
	}
}