package com.fred.signals;

public class SignalOut {

	private double frequency;
	private double pitch;

	public double getFrequency() {
		return frequency;
	}

	public void setFrequency(double frequency) {
		this.frequency = frequency;
	}

	public double getPitch() {
		return pitch;
	}

	public void setPitch(double pitch) {
		this.pitch = pitch;
	}

	public double getError() {
		return  Math.round(10000 * (pitch - Math.floor(pitch + 0.5))) / 100.0; 
	}
	
	public String getNote(){
		return NoteUtil.frequencyToNote(frequency);
	}

}
