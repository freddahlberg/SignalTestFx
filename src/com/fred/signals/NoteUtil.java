package com.fred.signals;

import java.awt.TextComponent;

public class NoteUtil extends Number {

	private static final double PITCH_OF_A4 = 57D;
	private static final double FACTOR = 12D / Math.log(2D);
	private static final String NOTE_SYMBOL[] = { "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B" };
	public static float frequencyOfA4 = 440F;
	private float frequency;

	public static double noteToFrequency(String noteSymbol){
		int noteNumber = noteToPitch(noteSymbol);
		return getFrequency(noteNumber);
	}
	
	public static String frequencyToNote(double frequency) {
		return makeNoteSymbol(getPitch(frequency));
	}
	
	public static final double getPitch(double d) {
		return 57D + FACTOR * Math.log(d / (double) frequencyOfA4);
	}

	public static final float getFrequency(double d) {
		return (float) (Math.exp((d - 57D) / FACTOR) * (double) frequencyOfA4);
	}

	public static final String makeNoteSymbol(double d) {
		int i = (int) (d + 120.5D);
		StringBuffer stringbuffer = new StringBuffer(NOTE_SYMBOL[i % 12]);
		stringbuffer.append(Integer.toString(i / 12 - 10));
		return new String(stringbuffer);
	}
	

	public static float valueOf(String s) throws IllegalArgumentException {
		try {
			return (new Float(s)).floatValue();
		}
		catch (NumberFormatException _ex) {
		}
		try {
			return getFrequency(noteToPitch(s));
		}
		catch (IllegalArgumentException _ex) {
			throw new IllegalArgumentException("Neither a floating point number nor a valid note symbol.");
		}
	}

	public static final int noteToPitch(String s) throws IllegalArgumentException {
		s = s.trim().toUpperCase();
		for (int i = NOTE_SYMBOL.length - 1; i >= 0; i--) {
			if (!s.startsWith(NOTE_SYMBOL[i]))
				continue;
			try {
				return i + 12 * Integer.parseInt(s.substring(NOTE_SYMBOL[i].length()).trim());
			}
			catch (NumberFormatException _ex) {
			}
			break;
		}

		throw new IllegalArgumentException("not valid note symbol.");
	}

	public static void transformPitch(TextComponent textcomponent, boolean flag) {
		boolean flag1 = false;
		String s = textcomponent.getText();
		if (flag) {
			try {
				textcomponent.setText(Integer.toString((int) (getFrequency(noteToPitch(s)) + 0.5F)));
				return;
			}
			catch (IllegalArgumentException _ex) {
				flag1 = true;
			}
			return;
		}
		try {
			textcomponent.setText(makeNoteSymbol(getPitch((new Float(s)).floatValue())));
			return;
		}
		catch (NumberFormatException _ex) {
			flag1 = true;
		}
	}

	public NoteUtil(float f) {
		frequency = 1.0F;
		frequency = f;
	}

	public NoteUtil(String s) throws IllegalArgumentException {
		frequency = 1.0F;
		frequency = valueOf(s);
	}

	public byte byteValue() {
		return (byte) (int) (frequency + 0.5F);
	}

	public short shortValue() {
		return (short) (int) (frequency + 0.5F);
	}

	public long longValue() {
		return (long) (frequency + 0.5F);
	}

	public int intValue() {
		return (int) (frequency + 0.5F);
	}

	public float floatValue() {
		return frequency;
	}

	public double doubleValue() {
		return (double) frequency;
	}

	public String toString() {
		return Integer.toString(intValue());
	}



	public static void main(String[] args) {
		System.out.println("A4 Freq: " + NoteUtil.noteToFrequency("A4") + ", Pitch: " + NoteUtil.noteToPitch("A4"));
		System.out.println("Bb4 Freq: " + NoteUtil.noteToFrequency("A#4") + ", Pitch: " + NoteUtil.noteToPitch("A#4"));
		
		double middleFrequency = (NoteUtil.noteToFrequency("A#4") + NoteUtil.noteToFrequency("A4")) /2;
		System.out.println("Pitch of frequency: " + middleFrequency + ", " +  NoteUtil.getPitch(middleFrequency));
			
		System.out.println(NoteUtil.noteToPitch("C#2"));
		System.out.println(NoteUtil.getFrequency(24));
		System.out.println(NoteUtil.getPitch(442));
		System.out.println(NoteUtil.makeNoteSymbol(57));
		
	}
}