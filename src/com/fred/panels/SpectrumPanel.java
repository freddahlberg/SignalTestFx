package com.fred.panels;

import java.util.List;

import com.fred.SignalComponentProperties;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Control;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

public class SpectrumPanel extends Pane{

	public SpectrumPanel(){

	}
	
	
	public void draw(List<SignalComponentProperties> signalComponents){
		
		this.getChildren().add(new Circle(150, Color.web("white")));
	}
	
	public void draw(float[] fftResponse, boolean logScale){
		
		this.getChildren().clear();
		Canvas canvas = new Canvas(getWidth(), getHeight());
		this.getChildren().add(canvas);
		
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setFill(Color.GREEN);
        gc.setStroke(Color.BLUE);
        gc.setLineWidth(1);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        
		// convert to magnitudes and find max magnitude
		double[] magnitudes = new double[fftResponse.length / 2];
		double maxMagnitude = 0;
		for (int i = 0; i < fftResponse.length / 2; i++) {
			double magnitude = Math.sqrt(Math.pow(fftResponse[i * 2], 2) + Math.pow(fftResponse[i * 2 + 1], 2));
			if(logScale){
				magnitudes[i] = Math.log(magnitude);
			}else{
				magnitudes[i] = magnitude;	
			}
			
			if(magnitudes[i] > maxMagnitude){
				maxMagnitude = magnitudes[i];
			}
		}
		
		double yScaleFactor = canvas.getHeight() / maxMagnitude;
		double xScaleFactor = canvas.getWidth() / (double)magnitudes.length;

		for (int i = 0; i < magnitudes.length; i++) {
			
			gc.strokeLine(i * xScaleFactor, canvas.getHeight(), i * xScaleFactor, canvas.getHeight() - magnitudes[i] * yScaleFactor);
		}
	}
	
}
