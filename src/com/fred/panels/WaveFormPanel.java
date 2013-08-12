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

public class WaveFormPanel extends Pane{

	public WaveFormPanel(){

	}
	
	
	public void draw(List<SignalComponentProperties> signalComponents){
		
		this.getChildren().add(new Circle(150, Color.web("white")));
	}
	
	public void draw(short[] signal){
		
		this.getChildren().clear();
		Canvas canvas = new Canvas(getWidth(), getHeight());
		this.getChildren().add(canvas);
		
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setFill(Color.GREEN);
        gc.setStroke(Color.BLUE);
        gc.setLineWidth(1);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        
        double yScaleFactor =  canvas.getHeight() / (2 * Math.pow(2, 15)) ;
        double xScaleFactor = canvas.getWidth() / (double)signal.length;
        
		for (int i = 0; i < signal.length - 1; i++) {

			gc.strokeLine(i * xScaleFactor, canvas.getHeight() / 2 - signal[i] * yScaleFactor , (i + 1) * xScaleFactor, canvas.getHeight() / 2 -  signal[i + 1] * yScaleFactor);
		}
	}
	
}
