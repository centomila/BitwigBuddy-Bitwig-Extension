package com.centomila;

public class Utils {

    // Constant with all noteNames
    public static String[] NOTE_NAMES = { "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B" };
    // Constant with all Octaves
    public static int[] NOTE_OCTAVES = { -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8 };

    /**
     * Returns a string representation of a MIDI note number as a note name and octave.
     * Example: 60 is returned as "C4"
     * @param midiNote the MIDI note number
     * @return a string representation of the MIDI note number
     */
    public static String getNoteNameAsString(int midiNote) {
        String[] noteNames = NOTE_NAMES;
        int octave = (midiNote / 12) - 2;
        String note = noteNames[midiNote % 12];
        return note + octave;
    }

    
    public static int getMIDINoteNumberFromStringAndOctave(String noteName, int octave) {
        String[] noteNames = NOTE_NAMES;
        int note = 0;
        for (int i = 0; i < noteNames.length; i++) {
            if (noteNames[i].equals(noteName)) {
                note = i;
                break;
            }
        }
        return (octave + 2) * 12 + note;
    }

}
