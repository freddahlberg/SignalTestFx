package com.fred.signals;

import java.util.List;

import com.fred.SignalComponentProperties;

public class SignalGenerator {

	boolean running = false;
	int word_size = 2;
	int mask = 0xff;
	int sbufsz = 2048;
	short[] sbuffer;
	int data_rate;
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

	public SignalGenerator() {

	}

	public short[] getSignal(int bufferSize, int samplingRate, List<SignalComponentProperties> signalComponents, boolean noiseEnabled, double noiseLevel) {

		short[] buffer = new short[bufferSize];

		sample_interval = 1.0 / samplingRate;

		double sumOfAmplitudes = 0;
		for (SignalComponentProperties props : signalComponents) {
			if (props.isActive()) {
				sumOfAmplitudes += props.getAmplitude();
			}
		}
		if(noiseEnabled){
			sumOfAmplitudes += noiseLevel;
		}

		for (int n = 0; n < bufferSize; n++) {
			double sample = 0;

			for (SignalComponentProperties props : signalComponents) {
				if (props.isActive()) {
					SigFunction sf = functs[props.getWaveForm()];
					sample += props.getAmplitude() * sf.f(props.getFrequency(), n * sample_interval);
				}
			}

			

			// Add noise if requested
			if (noiseEnabled) {
				double noise = (Math.random() - 0.5) * 2; // number between -1 and 1
				sample += noise * noiseLevel;
			}

			// Divide by sum of amplitudes so that signal varies between -1 and 1
			sample /= (sumOfAmplitudes);
						
			buffer[n] = (short) (sample * scale_mult);
		}

		return buffer;
	}

	public short[] getSignal(int waveform, double frequency, boolean modulationEnabled, int modulationWaveform, double modulationFrequency, double modulationLevel, int amFmMode,
			boolean noiseEnabled, double noiseLevel) {

		sbuffer = new short[sbufsz];

		data_rate = 44100;

		sample_interval = 1.0 / data_rate;
		running = true;
		double time_sec = 0;
		double osig, sig, noise, mod, fm_integral = 0;
		short ssig;
		SigFunction sf, mf;
		int n = 0;
		while (n < sbufsz) {
			sf = functs[waveform];
			if (!modulationEnabled) {
				sig = sf.f(frequency, time_sec);
			}
			else {
				mf = functs[modulationWaveform];
				mod = mf.f(modulationFrequency, time_sec) * modulationLevel;

				if (amFmMode == 0) {
					sig = 0.5 * sf.f(frequency, time_sec) * (1.0 + mod);
				}
				else {
					fm_integral += mod;
					sig = sf.f(frequency, (time_sec + fm_integral * sample_interval));
				}
			}
			osig = 0;
			if (noiseEnabled) {
				noise = (Math.random() - 0.5) * 2; // number between -1 and 1
				osig += noise * noiseLevel;
			}

			osig += sig;

			ssig = (short) (osig * scale_mult);
			sbuffer[n++] = ssig;

			time_sec += sample_interval;
		}

		return sbuffer;
	}
};

interface SigFunction {

	public double f(double f, double t);
};