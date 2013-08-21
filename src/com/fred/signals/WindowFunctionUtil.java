package com.fred.signals;

import com.fred.enums.WindowFunction;

public class WindowFunctionUtil {
	
	public static void applyToSignal(WindowFunction windowFunction, short[] signal){
		
		switch(windowFunction){
		case HAMMING:
		{
			int size = signal.length;
			for(int i = 0 ; i < signal.length ; i++){
				signal[i] = (short)((double)signal[i] * (0.54 - 0.46 * Math.cos((2 * Math.PI * i)/(size - 1))));
			}
			break;
		}
		case HANNING:
		{
			int size = signal.length;
			for(int i = 0 ; i < signal.length ; i++){
				signal[i] = (short)((double)signal[i] * 0.5 * (1 - Math.cos((2 * Math.PI * i)/(size - 1))));
			}
			break;
		}
		case RECTANGULAR:
			break;
		default:
			break;
		
		}
	}
}
