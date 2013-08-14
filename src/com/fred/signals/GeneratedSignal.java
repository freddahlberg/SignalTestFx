package com.fred.signals;

import java.util.List;

import com.fred.SignalComponentProperties;

public class GeneratedSignal {

	short[] signal;
	double snr;
	
	double sample_interval;
	double scale_mult = Math.pow(2, 15);
	SigFunction fsine = new SigFunction() {

		public double f(double f, double t) {
			return Math.sin(2.0 * Math.PI * f * t);
		}
	};
	SigFunction ftriangle = new SigFunction() {

		public double f(double f, double t) {
			double q = 4.0 * (t * f % 1.0);
			q = (q > 1.0) ? 2 - q : q;
			return (q < -1) ? -2 - q : q;
		}
	};
	SigFunction fsquare = new SigFunction() {

		public double f(double f, double t) {
			if (f == 0) {
				return 0;
			}
			double q = 0.5 - t * f % 1.0;
			return (q > 0) ? 1 : -1;
		}
	};
	SigFunction fsawtooth = new SigFunction() {

		public double f(double f, double t) {
			return 2.0 * (((t * f) + 0.5) % 1.0) - 1.0;
		}
	};
	SigFunction[] functs = new SigFunction[] { fsine, ftriangle, fsquare, fsawtooth };

	public GeneratedSignal(int bufferSize, int samplingRate, List<SignalComponentProperties> signalComponents, boolean noiseEnabled, double noiseLevel) {
		signal = new short[bufferSize];

		sample_interval = 1.0 / samplingRate;

		double sumOfAmplitudes = 0;
		for (SignalComponentProperties props : signalComponents) {
			if (props.isActive()) {
				sumOfAmplitudes += props.getAmplitude();
			}
		}
		if (noiseEnabled) {
			sumOfAmplitudes += noiseLevel;
		}

		double signalSquaredSum = 0;
		double noiseSquaredSum = 0;

		int time = (int)(Math.random() * samplingRate); // this is used so that the sample frame starts at a random point in time
		for (int n = 0; n < bufferSize; n++) {
			double sample = 0;

			for (SignalComponentProperties props : signalComponents) {
				if (props.isActive()) {
					SigFunction sf = functs[props.getWaveForm()];
					sample += props.getAmplitude() * sf.f(props.getFrequency(), (n + time) * sample_interval);
				}
			}

			signalSquaredSum += Math.pow(sample, 2);

			// Add noise if requested
			if (noiseEnabled) {
				double noise = (Math.random() - 0.5) * 2 * noiseLevel; // number between -1 and 1
				noiseSquaredSum += Math.pow(noise, 2);
				sample += noise;
			}

			// Divide by sum of amplitudes so that signal varies between -1 and 1
			sample /= (sumOfAmplitudes);

			signal[n] = (short) (sample * scale_mult);
		}

		if(noiseSquaredSum != 0){
			snr = signalSquaredSum / noiseSquaredSum;
		}
		else{
			snr = 0;
		}
	}

	public short[] getSignal() {
		return signal;
	}

	public void setSignal(short[] signal) {
		this.signal = signal;
	}

	public double getSnr() {
		return snr;
	}

	public void setSnr(double snr) {
		this.snr = snr;
	}
	
	public double getSnrInDB() {
		return 10 * Math.log10(snr);
	}

};

interface SigFunction {

	public double f(double f, double t);
};