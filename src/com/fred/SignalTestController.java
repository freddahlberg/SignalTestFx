package com.fred;

import java.util.ArrayList;
import java.util.List;

import com.fred.panels.SpectrumPanel;
import com.fred.panels.WaveFormPanel;
import com.fred.signals.NoteUtil;
import com.fred.signals.SignalGenerator;

import edu.emory.mathcs.jtransforms.fft.FloatFFT_1D;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

public class SignalTestController {

	@FXML
	Pane components;
	
	@FXML
	TextField txtFrameSize;
	
	@FXML
	CheckBox chkNoise;
	
	@FXML
	TextField txtNoiseLevel;
	
	@FXML
	TextField txtSamplingRate;
	
	@FXML
	WaveFormPanel signalPanel;
	
	@FXML
	SpectrumPanel spectrumPanel;

	private List<PropertiesPanel> properitesPanels;
	
	@FXML
	protected void handleUpdateButtonAction(ActionEvent event) {
		
		List<SignalComponentProperties> props = new ArrayList<SignalComponentProperties>();
		

		// get values from dialog
		
		for(PropertiesPanel pp : properitesPanels){
			props.add(pp.getSignalComponentProperties());
		}
		
		boolean noise = chkNoise.isSelected();
		double noiseLevel = Double.parseDouble(txtNoiseLevel.getText());
		
		int frameSize = Integer.parseInt(txtFrameSize.getText());
		int samplingRate = Integer.parseInt(txtSamplingRate.getText());
		
		
		// generate signal
		SignalGenerator sg = new SignalGenerator();
		short[] signal = sg.getSignal(frameSize, samplingRate, props, noise, noiseLevel);
		signalPanel.draw(signal);
		
		// fft
		// convert signal to float
		float[] floatSignal = new float[signal.length];
		for(int i = 0 ; i < signal.length ; i++){
			floatSignal[i] = (float)signal[i];
		}
		
		FloatFFT_1D fft = new FloatFFT_1D(signal.length);
		
		fft.realForward(floatSignal);
		
		spectrumPanel.draw(floatSignal);
		
		
		
		
	}

	@FXML
	protected void initialize() {
		
		List<SignalComponentProperties> signalComponents = new ArrayList<SignalComponentProperties>();
		signalComponents.add(new SignalComponentProperties(true, 0, NoteUtil.noteToFrequency("E2"), 1));
		signalComponents.add(new SignalComponentProperties(true, 0, NoteUtil.noteToFrequency("B2"), 1));
		signalComponents.add(new SignalComponentProperties(true, 0, NoteUtil.noteToFrequency("E3"), 1));
		signalComponents.add(new SignalComponentProperties(true, 0, NoteUtil.noteToFrequency("G#3"), 1));
		signalComponents.add(new SignalComponentProperties(true, 0, NoteUtil.noteToFrequency("B3"), 1));
		signalComponents.add(new SignalComponentProperties(true, 0, NoteUtil.noteToFrequency("E4"), 1));
		
		properitesPanels = new ArrayList<PropertiesPanel>();
		
		for (int i = 0; i < signalComponents.size(); i++) {
			PropertiesPanel pp = new PropertiesPanel();
			pp.setSignalComponentProperties(signalComponents.get(i));
			
			components.getChildren().add(pp);
			
			properitesPanels.add(pp);
		}
	}

	public Pane getComponents() {
		return components;
	}

	public void setComponents(Pane components) {
		this.components = components;
	}
}