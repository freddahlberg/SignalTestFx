package com.fred.signals;

import java.util.ArrayList;
import java.util.List;

public class SignalOut {

	private List<Double> frequencies= new ArrayList<Double>() ;
	
	public double getFrequency() {
		return frequencies.get(frequencies.size() - 1);
	}

	public void addFrequency(double frequency) {
		frequencies.add(frequency);
	}
	
	public double getMean(){
		double sum = 0;
		for(Double f : frequencies){
			sum += f;
		}		
		return sum / frequencies.size();
	}
	
	public double getStdDev(){
		double mean = getMean();
		double sumOfSquaredDiffs = 0;
		for(Double f : frequencies){
			sumOfSquaredDiffs += Math.pow(f-mean, 2);
		}
		return Math.sqrt(sumOfSquaredDiffs / frequencies.size());
	}

	public double getPitch() {
		return NoteUtil.getPitch(frequencies.get(frequencies.size() - 1));
	}

	public double getError() {
		double pitch = getPitch();
		return  Math.round(10000 * (pitch - Math.floor(pitch + 0.5))) / 100.0; 
	}
	
	public String getNote(){
		return NoteUtil.frequencyToNote(getMean());
	}

}
