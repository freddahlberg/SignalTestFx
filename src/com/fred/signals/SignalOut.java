package com.fred.signals;

import java.util.ArrayList;
import java.util.List;

public class SignalOut {

	private List<Double> frequencies= new ArrayList<Double>() ;
	private List<Double> errors= new ArrayList<Double>() ;

	public SignalOut(){
	}
	
	public SignalOut(SignalOut so){
		this.frequencies = so.frequencies;
		this.errors = so.errors;
	}
	
	public double getLastFrequency() {
		return frequencies.get(frequencies.size() - 1);
	}

	public void addFrequency(double frequency) {
		frequencies.add(frequency);
		errors.add(getError(frequency));
	}
	
	public double getMeanError(){
		double sum = 0;
		for(Double f : errors){
			sum += f;
		}		
		return sum / errors.size();
	}
	
	public double getStdDevError(){
		double mean = getMeanError();
		double sumOfSquaredDiffs = 0;
		for(Double f : errors){
			sumOfSquaredDiffs += Math.pow(f-mean, 2);
		}
		return Math.sqrt(sumOfSquaredDiffs / errors.size());
	}

	public double getLastPitch() {
		return NoteUtil.getPitch(frequencies.get(frequencies.size() - 1));
	}

	public double getLastError() {
		double pitch = getLastPitch();
		return  Math.round(10000 * (pitch - Math.floor(pitch + 0.5))) / 100.0; 
	}
	
	public String getLastNote(){
		return NoteUtil.frequencyToNote(getLastFrequency());
	}
	
	private double getError(double frequency) {
		double pitch = getPitch(frequency);
		return  Math.round(10000 * (pitch - Math.floor(pitch + 0.5))) / 100.0; 
	}
	
	private double getPitch(double frequency) {
		return NoteUtil.getPitch(frequency);
	}

}
