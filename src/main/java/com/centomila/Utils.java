package com.centomila;

public class Utils {

    public static String getNoteNameAsString(int midiNote) {
        String[] noteNames = { "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B" };
        int octave = (midiNote / 12) - 2;
        String note = noteNames[midiNote % 12];
        return note + octave;
    }

}
