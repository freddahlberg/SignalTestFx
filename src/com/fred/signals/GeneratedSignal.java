package com.fred.signals;

import java.util.List;

import com.fred.SignalComponentProperties;
import com.fred.enums.WindowFunction;

public class GeneratedSignal {

	short[] signal;
	double snr;

	double sample_interval;
	double scale_mult = Math.pow(2, 15);
	
	int frameSize;
	int samplingRate;
	
	
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

	public GeneratedSignal(int frameSize, int samplingRate, List<SignalComponentProperties> signalComponents, boolean noiseEnabled, double snrIn, WindowFunction windowFunction) {
		
		this.frameSize = frameSize;
		this.samplingRate = samplingRate;
		
		signal = new short[frameSize];
		double[] tempSignal = new double[frameSize];
		
		sample_interval = 1.0 / samplingRate;

		double maxAmplitude = 0;
		for (SignalComponentProperties props : signalComponents) {
			if (props.isActive()) {
				maxAmplitude += props.getAmplitude();
			}
		}

		int time = (int)(Math.random() * samplingRate); // this is used so that the sample frame starts at a random point in time
		for (int n = 0; n < frameSize; n++) {
			double sample = 0;

			for (SignalComponentProperties props : signalComponents) {
				if (props.isActive()) {
					SigFunction sf = functs[props.getWaveForm()];
					sample += props.getAmplitude() * sf.f(props.getFrequency(), (n + time) * sample_interval);
				}
			}
			
			tempSignal[n] = sample;
		}
		
		
		// Add noise if requested
		if (noiseEnabled) {
			// Calculate signal energy
			double signalSquaredSum = 0;
			double noiseSquaredSum = 0;
			
			for(double sample : tempSignal){
				signalSquaredSum += Math.pow(sample, 2);
			}
			
			// calculate required noise level to get the desired SNR
			double noiseLevel = Math.sqrt(3 * signalSquaredSum / (snrIn * frameSize));
			
			for(int i = 0 ; i < tempSignal.length ; i++){
				double noise = (Math.random() - 0.5) * 2 * noiseLevel; // number between -noiseLevel and noiseLevel
				noiseSquaredSum += Math.pow(noise, 2);
				tempSignal[i] += noise;
			}

			// Control calculation of the snr
			if(noiseSquaredSum != 0){
				snr = signalSquaredSum / noiseSquaredSum;
			}
			else{
				snr = 0;
			}
			
			maxAmplitude += noiseLevel;
		}
		
		// scale the signal
		for(int i = 0 ; i < tempSignal.length ; i++){
			signal[i] = (short) (scale_mult * tempSignal[i] / maxAmplitude);
		}
		
		// Apply window if not rectangular window
		if(windowFunction == WindowFunction.HANNING){
			
			int size = signal.length;
			for(int i = 0 ; i < signal.length ; i++){
				signal[i] = (short)((double)signal[i] * 0.5 * (1 - Math.cos((2 * Math.PI * i)/(size - 1))));
			}
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

	public int getFrameSize() {
		return frameSize;
	}

	public int getSamplingRate() {
		return samplingRate;
	}

};

interface SigFunction {

	public double f(double f, double t);
};