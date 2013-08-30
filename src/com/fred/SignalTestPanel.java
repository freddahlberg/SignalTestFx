package com.fred;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;

import com.fred.dsp.enums.InterpolationTechnique;
import com.fred.dsp.enums.WindowFunction;
import com.fred.dsp.signals.Complex;
import com.fred.dsp.signals.FFTUtil;
import com.fred.dsp.signals.GeneratedSignal;
import com.fred.dsp.signals.NoteUtil;
import com.fred.dsp.signals.SignalComponentProperties;
import com.fred.dsp.signals.SignalOut;
import com.fred.panels.SpectrumPanel;
import com.fred.panels.WaveFormPanel;
import com.fred.signals.SignalListener;
import com.fred.signals.SignalRecordTask;

import edu.emory.mathcs.jtransforms.fft.FloatFFT_1D;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.SortType;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

public class SignalTestPanel extends GridPane implements SignalListener{

	
	@FXML
	Pane components;

	@FXML
	TextField txtFrameSize;

	@FXML
	CheckBox chkNoise;

	@FXML
	TextField txtSNRIn;

	@FXML
	TextField txtSamplingRate;

	@FXML
	WaveFormPanel signalPanel;

	@FXML
	SpectrumPanel spectrumPanel;

	@FXML
	TableView<SignalOut> tableResults;
	
	@FXML
	ComboBox<WindowFunction> cmbWindowFunction;
	
	@FXML
	ComboBox<InterpolationTechnique> cmbInterpolationTechniques;

	@FXML
	TextField txtSNR;

	@FXML
	TextField txtSNRInDB;
	
	@FXML
	TextField txtBinWidth;

	//	@FXML
	//	TableColumn<> colFrequency;

	private List<PropertiesPanel> propertiesPanels;

	private Thread signalRecorderthread;

	private Map<String, SignalOut> lastSignals = new HashMap<String, SignalOut>();
	
	
	public SignalTestPanel() {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("signal_test_panel.fxml"));
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
	protected void handleStartButtonAction(ActionEvent event) {
		
		List<SignalComponentProperties> props = new ArrayList<SignalComponentProperties>();
		
		// get values from dialog
		for (PropertiesPanel pp : propertiesPanels) {
			props.add(pp.getSignalComponentProperties());
		}

		boolean noise = chkNoise.isSelected();
		double snrIn = Double.parseDouble(txtSNRIn.getText());

		int frameSize = Integer.parseInt(txtFrameSize.getText());
		int samplingRate = Integer.parseInt(txtSamplingRate.getText());
		
		final SignalRecordTask task = new SignalRecordTask(this, frameSize, samplingRate, props, noise, snrIn, cmbWindowFunction.getValue());

		signalRecorderthread = new Thread(task);
		signalRecorderthread.setDaemon(true);
		signalRecorderthread.start();
		
	}

	@FXML
	protected void handleStopButtonAction(ActionEvent event) {
		signalRecorderthread.interrupt();
	}
	
	@FXML
	protected void handleUpdateButtonAction(ActionEvent event) {
		
		GeneratedSignal gs = getSignal();
		update(gs);
	}
	
	@FXML
	protected void handleResetButtonAction(ActionEvent event) {
		lastSignals = new HashMap<String, SignalOut>();
	}
	
	
	private GeneratedSignal getSignal(){
		
		List<SignalComponentProperties> props = new ArrayList<SignalComponentProperties>();
		
		// get values from dialog
		for (PropertiesPanel pp : propertiesPanels) {
			props.add(pp.getSignalComponentProperties());
		}

		boolean noise = chkNoise.isSelected();
		double snrIn = Double.parseDouble(txtSNRIn.getText());

		int frameSize = Integer.parseInt(txtFrameSize.getText());
		int samplingRate = Integer.parseInt(txtSamplingRate.getText());

		// generate signal
		GeneratedSignal gs = new GeneratedSignal(frameSize, samplingRate, props, noise, snrIn, cmbWindowFunction.getValue());
		
		return gs;
		
	}
	
	private void update(GeneratedSignal gs){
		
		
		short[] signal = gs.getSignal();
		signalPanel.draw(signal);
		txtSNR.setText(Double.toString(gs.getSnr()));
		txtSNRInDB.setText(Double.toString(gs.getSnrInDB()));
		txtBinWidth.setText(Double.toString(gs.getSamplingRate() / (double)gs.getFrameSize()));
		
		// fft
		// convert signal to float
		float[] floatSignal = new float[signal.length];
		for (int i = 0; i < signal.length; i++) {
			floatSignal[i] = (float) signal[i];
		}

		FloatFFT_1D fft = new FloatFFT_1D(signal.length);

		fft.realForward(floatSignal);

		spectrumPanel.draw(floatSignal, true);

		// Find main frequencies

		Complex[] fftResponse = FFTUtil.getComplexFFTResponse(floatSignal);
		// count number of active input frequencies
		int activeInputsCount = 6;
		
		double[] mainBinFrequencies = FFTUtil.getMainFrequencies(fftResponse, activeInputsCount, cmbInterpolationTechniques.getValue());

		double[] mainFrequencies = new double[mainBinFrequencies.length];
		for (int i = 0; i < mainBinFrequencies.length; i++) {
			mainFrequencies[i] = FFTUtil.getFrequencyFromFFTBin(mainBinFrequencies[i], gs.getSamplingRate(), gs.getFrameSize());
		}

		// Update output data in table
		Map<String, SignalOut> tempLastSignals = new HashMap<String, SignalOut>();
		for(Entry<String, SignalOut> entry : lastSignals.entrySet()){
			tempLastSignals.put(entry.getKey(), entry.getValue());
		}

		// Update output data in table
		lastSignals.clear();
		
		tableResults.getItems().removeAll(tableResults.getItems());
		for (int i = 0; i < mainFrequencies.length; i++) {
			SignalOut so;
			// Did we have this note last time?
			// If we did we want to see the average of the measurements.
			String note = NoteUtil.frequencyToNote(mainFrequencies[i]);
			if (tempLastSignals.containsKey(note)) {
				SignalOut soTemp = tempLastSignals.get(note);
				so = new SignalOut(soTemp);
			}else{
				so = new SignalOut();
			}

			so.addFrequency(mainFrequencies[i]);
			tableResults.getItems().add(so);
			lastSignals.put(note, so);
		}

		TableColumn<SignalOut, ?> sortColumnn = tableResults.getColumns().get(0);
		tableResults.getSortOrder().add(sortColumnn);
		sortColumnn.setSortType(SortType.ASCENDING);
		sortColumnn.setSortable(true); // This performs a sort
	}

	@FXML
	protected void initialize() {

		// Initialize signal components
		List<SignalComponentProperties> signalComponents = new ArrayList<SignalComponentProperties>();
		signalComponents.add(new SignalComponentProperties(true, 0, NoteUtil.noteToFrequency("E2"), 1));
		signalComponents.add(new SignalComponentProperties(true, 0, NoteUtil.noteToFrequency("F2"), 1));
		signalComponents.add(new SignalComponentProperties(true, 0, NoteUtil.noteToFrequency("E3"), 1));
		signalComponents.add(new SignalComponentProperties(true, 0, NoteUtil.noteToFrequency("G#3"), 1));
		signalComponents.add(new SignalComponentProperties(true, 0, NoteUtil.noteToFrequency("B3"), 1));
		signalComponents.add(new SignalComponentProperties(true, 0, NoteUtil.noteToFrequency("E4"), 1));

		propertiesPanels = new ArrayList<PropertiesPanel>();

		for (int i = 0; i < signalComponents.size(); i++) {
			PropertiesPanel pp = new PropertiesPanel();
			pp.setSignalComponentProperties(signalComponents.get(i));

			components.getChildren().add(pp);

			propertiesPanels.add(pp);
		}
		
		// Initialize combo box for window functions
		for (WindowFunction wf : WindowFunction.values()) {
			cmbWindowFunction.getItems().add(wf);
		}
		cmbWindowFunction.getSelectionModel().select(0);
		

		// Initialize combo box for interpolation techniques
		for (InterpolationTechnique ip : InterpolationTechnique.values()) {
			cmbInterpolationTechniques.getItems().add(ip);
		}
		cmbInterpolationTechniques.getSelectionModel().select(0);

	}

	public Pane getComponents() {
		return components;
	}

	public void setComponents(Pane components) {
		this.components = components;
	}

	@Override
	public void onSignalUpdated(final GeneratedSignal gs) {
		
		Platform.runLater(new Runnable() {
            @Override public void run() {
            	update(gs);
            }
        });
	}
}