package com.fred.signals;

public class FFTUtil {

	
	public static double getFrequencyFromFFTBin(double fftBin, double samplingRate, int frameSize){
		
		return fftBin * samplingRate / frameSize;
	}
	
	
	public static Complex[] getComplexFFTResponse(float[] fftResponse) {

		Complex[] complexFFTResponse = new Complex[fftResponse.length / 2];
		for (int i = 0; i < fftResponse.length / 2; i++) {
			complexFFTResponse[i] = new Complex(fftResponse[i * 2], fftResponse[i * 2 + 1]);
		}

		return complexFFTResponse;
	}

	/**
	 * This method uses interpolation described as number 3 in
	 * http://www.ingelec
	 * .uns.edu.ar/pds2803/Materiales/Articulos/AnalisisFrecuencial/04205098.pdf
	 * to find the frequencies with highest magnitudes. The frequencies are
	 * returned in bins.
	 * 
	 * @param fftResponse
	 * @return
	 */
	public static double[] getMainFrequencies(Complex[] fftResponse, int numOfFreqeuncies) {

		double[] mainFrequencies = new double[numOfFreqeuncies];
		double[] magnitudes = new double[fftResponse.length / 2];

		// get magnitudes of frequencies
		for (int i = 0; i < fftResponse.length / 2; i++) {
			magnitudes[i] = fftResponse[i].abs();
		}

		for (int freq = 0; freq < numOfFreqeuncies; freq++) {
			// find max magnitude (we call it beta)
			int betaPos = 0;

			for (int i = 0; i < fftResponse.length / 2; i++) {
				if (magnitudes[i] > magnitudes[betaPos]) {
					betaPos = i;
				}
			}
			if (betaPos == 0) {
				break;
			}
			Complex beta = fftResponse[betaPos];
			Complex alpha = fftResponse[betaPos - 1];
			Complex gamma = fftResponse[betaPos + 1];

			// Calculate the correction (delta) 
			double delta = gamma.minus(alpha).divides(beta.times(2).minus(alpha.plus(gamma))).re();

			mainFrequencies[freq] = betaPos - delta;

			// Remove the found magnitude peak so that we can find the next one 
			magnitudes[betaPos - 1] = 0;
			magnitudes[betaPos] = 0;
			magnitudes[betaPos + 1] = 0;
		}

		return mainFrequencies;


	}

}
