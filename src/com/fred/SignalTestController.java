package com.fred;

import java.util.ArrayList;
import java.util.List;

import com.fred.enums.InterpolationTechnique;
import com.fred.panels.SpectrumPanel;
import com.fred.panels.WaveFormPanel;
import com.fred.signals.Complex;
import com.fred.signals.FFTUtil;
import com.fred.signals.NoteUtil;
import com.fred.signals.GeneratedSignal;
import com.fred.signals.SignalOut;

import edu.emory.mathcs.jtransforms.fft.FloatFFT_1D;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.SortType;
import javafx.scene.control.TableView;
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
	ComboBox<InterpolationTechnique> cmbInterpolationTechniques;

	@FXML
	TextField txtSNR;

	@FXML
	TextField txtSNRInDB;

	//	@FXML
	//	TableColumn<> colFrequency;

	private List<PropertiesPanel> propertiesPanels;

	@FXML
	protected void handleUpdateButtonAction(ActionEvent event) {

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
		GeneratedSignal sg = new GeneratedSignal(frameSize, samplingRate, props, noise, snrIn);
		short[] signal = sg.getSignal();
		signalPanel.draw(signal);
		txtSNR.setText(Double.toString(sg.getSnr()));
		txtSNRInDB.setText(Double.toString(sg.getSnrInDB()));

		// fft
		// convert signal to float
		float[] floatSignal = new float[signal.length];
		for (int i = 0; i < signal.length; i++) {
			floatSignal[i] = (float) signal[i];
		}

		FloatFFT_1D fft = new FloatFFT_1D(signal.length);

		fft.realForward(floatSignal);

		spectrumPanel.draw(floatSignal);

		// Find main frequencies

		Complex[] fftResponse = FFTUtil.getComplexFFTResponse(floatSignal);
		// count number of active input frequencies
		int activeInputsCount = 0;
		for (SignalComponentProperties sgp : props) {
			if (sgp.isActive()) {
				activeInputsCount++;
			}
		}
		double[] mainBinFrequencies = FFTUtil.getMainFrequencies(fftResponse, activeInputsCount, cmbInterpolationTechniques.getValue());

		double[] mainFrequencies = new double[mainBinFrequencies.length];
		for (int i = 0; i < mainBinFrequencies.length; i++) {
			mainFrequencies[i] = FFTUtil.getFrequencyFromFFTBin(mainBinFrequencies[i], samplingRate, frameSize);
		}

		// Update output data in table
		tableResults.getItems().removeAll(tableResults.getItems());
		for (int i = 0; i < mainFrequencies.length; i++) {
			SignalOut so = new SignalOut();
			so.setFrequency(mainFrequencies[i]);
			so.setPitch(NoteUtil.getPitch(mainFrequencies[i]));
			tableResults.getItems().add(so);
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
		signalComponents.add(new SignalComponentProperties(true, 0, NoteUtil.noteToFrequency("B2"), 1));
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
}