package com.fred.signals;

import com.fred.enums.InterpolationTechnique;
import com.fred.enums.WindowFunction;

public class FFTUtil {

	public static double getFrequencyFromFFTBin(double fftBin, double samplingRate, int frameSize) {

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
	public static double[] getMainFrequencies(Complex[] fftResponse, int numOfFrequencies, InterpolationTechnique interpolationTechnique) {

		double[] mainFrequencies = new double[numOfFrequencies];
		double[] magnitudes = new double[fftResponse.length];

		// get magnitudes of frequencies
		for (int i = 0; i < fftResponse.length; i++) {
			magnitudes[i] = fftResponse[i].abs();
		}

		// set dc to 0
		magnitudes[0] = 0;
 
		for (int freq = 0; freq < numOfFrequencies; freq++) {
			// find max magnitude (we call it beta)
			int betaPos = 0;

			// Iterate through all the fft bins.
			for (int i = 0; i < fftResponse.length; i++) {
				if (magnitudes[i] > magnitudes[betaPos]) {
					betaPos = i;
				}
			}
			if (betaPos == 0) {
				break;
			}

			// Calculate the correction (delta) 
			double delta = calculateCorrection(fftResponse[betaPos - 1], fftResponse[betaPos], fftResponse[betaPos + 1], interpolationTechnique, fftResponse.length * 2);// gamma.minus(alpha).divides(beta.times(2).minus(alpha.plus(gamma))).re();

			mainFrequencies[freq] = betaPos - delta;

			// Remove the found magnitude peak so that we can find the next one 
			magnitudes[betaPos - 1] = 0;
			magnitudes[betaPos] = 0;
			magnitudes[betaPos + 1] = 0;
		}

		return mainFrequencies;

	}

	private static double calculateCorrection(Complex alpha, Complex beta, Complex gamma, InterpolationTechnique interpolationTechnique, int frameSize) {

		switch (interpolationTechnique) {
		case JACOBSEN_3:
			return calcJacobsen3(alpha, beta, gamma);
		case JACOBSEN_3_HANNNING:
			return 2 * calcJacobsen3(alpha, beta, gamma);
		case JACOBSEN_3_HAMMING:
			return 1.81818 * calcJacobsen3(alpha, beta, gamma);
		case JACOBSEN_5_HANNING:
			return calcJacobsen5(alpha, beta, gamma, WindowFunction.HANNING);
		case JACOBSEN_5_HAMMING:
			return calcJacobsen5(alpha, beta, gamma, WindowFunction.HAMMING);
		case JACOBSEN_WITH_BIAS_CORRECTION:
			return (Math.tan(Math.PI/frameSize)/(Math.PI/frameSize)) * calcJacobsen3(alpha, beta, gamma);
		case QUINN1:
		{
			// from http://www.dspguru.com/dsp/howtos/how-to-interpolate-fft-peak
//			ap = (X[k + 1].r * X[k].r + X[k + 1].i * X[k].i)  /  (X[k].r * X[k].r + X[k].i * X[k].i)
//			dp = -ap  / (1.0 - ap)
//			am = (X[k - 1].r * X[k].r + X[k - 1].i * X[k].i)  /  (X[k].r * X[k].r + X[k].i * X[k].i)
//			dm = am / (1.0 - am)
//			if (dp > 0) AND (dm > 0) then 
//			   d = dp
//			else
//			   d = dm
//			end
//			k' = k + d
//			
			double ap = (gamma.re() * beta.re() + gamma.im() * beta.im())  /  (beta.re() * beta.re() + beta.im() * beta.im());
			double dp = -ap  / (1.0 - ap);
			double am = (alpha.re() * beta.re() + alpha.im() * beta.im())  /  (beta.re() * beta.re() + beta.im() * beta.im());
			double dm = am / (1.0 - am);
			double d;
			if (dp > 0 && dm > 0){
			   d = dp;
			}
			else{
			   d = dm;
			}
			return -d;
		}
		case QUINN2:
		{
//			double betam = alpha.divides(beta).re();// real(Y(1)/Y(2));
//			double betap = gamma.divides(beta).re();
//
//			double dm = -betam / (betam - 1);
//			double dp = betap / (betap - 1);
//
//			double xp =  Math.pow(dp, 2);
//			double kappap = 0.25 * Math.log(3 * Math.pow(xp, 2) + 6 * xp + 1) - (Math.sqrt(6) / 24)	* Math.log((xp + 1 - Math.sqrt(2 / 3)) / (xp + 1 + Math.sqrt(2 / 3)));
//			
//			double xm =  Math.pow(dm, 2);
//			double kappam = 0.25 * Math.log(3 * Math.pow(xm, 2) + 6 * xm + 1) - (Math.sqrt(6) / 24)	* Math.log((xm + 1 - Math.sqrt(2 / 3)) / (xm + 1 + Math.sqrt(2 / 3)));
//
//			return (dm + dp) / 2 + kappap - kappam;
			
			double ap = (gamma.re() * beta.re() + gamma.im() * beta.im())  /  (beta.re() * beta.re() + beta.im() * beta.im());
			double dp = -ap / (1 - ap);
			double am = (alpha.re() * beta.re() + alpha.im() * beta.im())  /  (beta.re() * beta.re() + beta.im() * beta.im());
			double dm = am / (1 - am);
			double d = (dp + dm) / 2 + tau(dp * dp) - tau(dm * dm);
			return -d;
		}
		default:
			break;

		}
		return 0;
	}

	private static double calcJacobsen3(Complex alpha, Complex beta, Complex gamma){
		return gamma.minus(alpha).divides(beta.times(2).minus(alpha.plus(gamma))).re();
	}

	private static double calcJacobsen5(Complex alpha, Complex beta, Complex gamma, WindowFunction windowFunction){
		double q = 1;
		switch(windowFunction){
		case HAMMING:
			q = .60;
			break;
		case HANNING:
			q = .55;
			break;
		default:
			break;
		
		}
		
		return -alpha.minus(gamma).times(q).divides(beta.times(2).plus(alpha.plus(gamma))).re();
	}
	
	private static double tau(double x){
		return 0.25 * Math.log(3 * Math.pow(x, 2) + 6 * x + 1) - (Math.sqrt(6) / 24)	* Math.log((x + 1 - Math.sqrt(2 / 3)) / (x + 1 + Math.sqrt(2 / 3)));
	}
}


//tau(x) = 1/4 * log(3x^2 + 6x + 1) - sqrt(6)/24 * log((x + 1 - sqrt(2/3))  /  (x + 1 + sqrt(2/3)))
//
//ap = (X[k + 1].r * X[k].r + X[k+1].i * X[k].i)  /  (X[k].r * X[k].r + X[k].i * X[k].i)
//dp = -ap / (1 - ap)
//am = (X[k - 1].r * X[k].r + X[k - 1].i * X[k].i)  /  (X[k].r * X[k].r + X[k].i * X[k].i)
//dm = am / (1 - am)
//d = (dp + dm) / 2 + tau(dp * dp) - tau(dm * dm)
//k' = k + d
//
//
//betam=real(Y(1)/Y(2));
//betap=real(Y(3)/Y(2));
//
//dm=-betam/(betam-1);
//dp= betap/(betap-1);
//
//kappap=(1/4) * log(3*(dp^4)+6*(dp^2)+1) - (sqrt(6)/24) * log(((dp^2) + 1 - sqrt(2/3)) / ((dp^2) + 1 + sqrt(2/3)));
//kappam=(1/4) * log(3*(dm^4)+6*(dm^2)+1) - (sqrt(6)/24) * log(((dm^2) + 1 - sqrt(2/3)) / ((dm^2) + 1 + sqrt(2/3)));
//
//delta=(dm+dp)/2 +kappap-kappam;
//
//x=delta;

