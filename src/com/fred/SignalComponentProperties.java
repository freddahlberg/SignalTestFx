package com.fred;

public class SignalComponentProperties {
	private boolean active;
	private int waveForm;
	private double frequency;
	private double amplitude;
	
	public SignalComponentProperties() {
		super();
	}
	
	public SignalComponentProperties(boolean active, int waveForm, double frequency, double amplitude) {
		super();
		this.active = active;
		this.waveForm = waveForm;
		this.frequency = frequency;
		this.amplitude = amplitude;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public int getWaveForm() {
		return waveForm;
	}

	public void setWaveForm(int waveForm) {
		this.waveForm = waveForm;
	}

	public double getFrequency() {
		return frequency;
	}

	public void setFrequency(double frequency) {
		this.frequency = frequency;
	}

	public double getAmplitude() {
		return amplitude;
	}

	public void setAmplitude(double amplitude) {
		this.amplitude = amplitude;
	}

}
