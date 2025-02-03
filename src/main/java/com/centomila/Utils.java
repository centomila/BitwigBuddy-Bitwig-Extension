package com.centomila;

public class Utils {

    /**
     * Returns a string representation of a MIDI note number as a note name and octave.
     * Example: 60 is returned as "C4"
     * @param midiNote the MIDI note number
     * @return a string representation of the MIDI note number
     */
    public static String getNoteNameAsString(int midiNote) {
        String[] noteNames = { "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B" };
        int octave = (midiNote / 12) - 2;
        String note = noteNames[midiNote % 12];
        return note + octave;
    }

}
