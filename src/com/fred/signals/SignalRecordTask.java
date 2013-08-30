package com.fred.signals;

import java.util.List;

import com.fred.dsp.enums.WindowFunction;
import com.fred.dsp.signals.GeneratedSignal;
import com.fred.dsp.signals.SignalComponentProperties;

import sun.misc.Signal;
import javafx.concurrent.Task;

public class SignalRecordTask extends Task<GeneratedSignal> {

	private int frameSize;
	private int samplingRate;
	private List<SignalComponentProperties> signalComponents;
	private boolean noiseEnabled;
	private double snrIn;
	private WindowFunction windowFunction;
	private short[] signal;
	private SignalListener listener;

	public SignalRecordTask(SignalListener listener, int frameSize, int samplingRate, List<SignalComponentProperties> signalComponents, boolean noiseEnabled, double snrIn,
			WindowFunction windowFunction) {
		this.listener = listener;
		this.frameSize = frameSize;
		this.samplingRate = samplingRate;
		this.signalComponents = signalComponents;
		this.noiseEnabled = noiseEnabled;
		this.snrIn = snrIn;
		this.windowFunction = windowFunction;
	}

	@Override
	protected GeneratedSignal call() throws Exception {

		while (!isCancelled()) {
			System.out.println("Generating signal");

			GeneratedSignal gs = new GeneratedSignal(frameSize, samplingRate, signalComponents, noiseEnabled, snrIn, windowFunction);
			listener.onSignalUpdated(gs);
			Thread.sleep(100);
		}
		return null;
	}

	public short[] getSignal() {
		return signal;
	}

}
