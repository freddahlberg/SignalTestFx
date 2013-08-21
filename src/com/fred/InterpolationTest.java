package com.fred;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.text.Position.Bias;

import com.fred.enums.InterpolationTechnique;
import com.fred.enums.WindowFunction;
import com.fred.signals.Complex;
import com.fred.signals.FFTUtil;
import com.fred.signals.GeneratedSignal;
import com.fred.signals.NoteUtil;
import com.fred.signals.SignalRecordTask;

import edu.emory.mathcs.jtransforms.fft.FloatFFT_1D;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class InterpolationTest extends GridPane {

	@FXML
	ComboBox<WindowFunction> cmbWindowFunction;
	
	@FXML
	ComboBox<InterpolationTechnique> cmbInterpolationTechniques;
	
	@FXML
	TextField txtFromBin;

	@FXML
	TextField txtToBin;
	
	@FXML
	TextField txtStep;
	
	@FXML
	TextField txtFrameSize;

	@FXML
	CheckBox chkNoise;

	@FXML
	TextField txtSNRIn;

	@FXML
	TextField txtSamplingRate;
	
	@FXML
	LineChart<Number, Number> chartBias;
	
	
	public InterpolationTest() {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("interpolation_test.fxml"));
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
	protected void initialize() {
		
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
		
		// set up chart
		chartBias.setAnimated(false);
		chartBias.getXAxis().setAutoRanging(false);
		chartBias.getXAxis().setTickLabelsVisible(true);
		chartBias.getYAxis().setAutoRanging(true);
		NumberAxis yAxis = (NumberAxis)chartBias.getYAxis();
//		yAxis.setLowerBound(-.3);
//		yAxis.setUpperBound(0.3);
		yAxis.setTickLength(0.1);
		yAxis.setTickMarkVisible(true);
		chartBias.setCreateSymbols(false);
	}
	
	@FXML
	protected void handleStartButtonAction(ActionEvent event) {
		
		int frameSize = Integer.parseInt(txtFrameSize.getText());;
		int samplingRate = Integer.parseInt(txtSamplingRate.getText());
		
		List<SignalComponentProperties> signalComponents = new ArrayList<SignalComponentProperties>();
	
		SignalComponentProperties scp = new SignalComponentProperties(true, 0, 80, 1);
		
		double fromBin = Double.parseDouble(txtFromBin.getText());
		double toBin = Double.parseDouble(txtToBin.getText());
		double step = Double.parseDouble(txtStep.getText());
		
		WindowFunction windowFunction = cmbWindowFunction.getValue();
		InterpolationTechnique  interpolationTechnique = cmbInterpolationTechniques.getValue();
		
		// Set up Chart
		XYChart.Series<Number, Number> biasSeries = new Series<>();
		biasSeries.setName("bias");
		
		XYChart.Series<Number, Number> measuredSeries = new Series<>();
		measuredSeries.setName("measured");
		

		
		// Iterate between the bins
		for(double bin = fromBin ; bin < toBin ; bin += step){
			
			// Generate a signal with a frequency at fft bin pos
			signalComponents.clear();
			double frequency = FFTUtil.getFrequencyFromFFTBin(bin, samplingRate, frameSize);
			signalComponents.add(new SignalComponentProperties(true, 0, frequency, 1));
			
			GeneratedSignal gs = new GeneratedSignal(frameSize, samplingRate, signalComponents, false, 10, windowFunction);
			
			// Calculate FFT
			short[] signal = gs.getSignal();
			
			float[] floatSignal = new float[signal.length];
			for (int i = 0; i < signal.length; i++) {
				floatSignal[i] = (float) signal[i];
			}

			FloatFFT_1D fft = new FloatFFT_1D(signal.length);

			fft.realForward(floatSignal);
			
			// Find main frequency
			Complex[] fftResponse = FFTUtil.getComplexFFTResponse(floatSignal);
			double[] mainBinFrequencies = FFTUtil.getMainFrequencies(fftResponse, 1, interpolationTechnique);
			
			double bias = bin - mainBinFrequencies[0];
			
			biasSeries.getData().add(new XYChart.Data<Number, Number>(bin, bias));
			
			measuredSeries.getData().add(new XYChart.Data<Number, Number>(bin, mainBinFrequencies[0]));
		}
		
		chartBias.getData().add(biasSeries);
//		chartBias.getData().add(measuredSeries);
		NumberAxis xAxis = (NumberAxis)chartBias.getXAxis();
		xAxis.setLowerBound(fromBin);
		xAxis.setUpperBound(toBin);
	}
	
	@FXML
	protected void handleResetButtonAction(ActionEvent event) {
		while(chartBias.getData().size() > 0){
			chartBias.getData().remove(0);
		}
	}

	
}