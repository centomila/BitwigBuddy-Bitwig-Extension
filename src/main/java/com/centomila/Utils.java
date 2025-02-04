package com.centomila;

public class Utils {

    // Constant with all noteNames
    public static String[] NOTE_NAMES = { "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B" };
    // Constant with all Octaves
    public static int[] NOTE_OCTAVES = { -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8 };

    /**
     * Returns a string representation of a MIDI note number as a note name and
     * octave.
     * Example: 60 is returned as "C4"
     * 
     * @param midiNote the MIDI note number
     * @return a string representation of the MIDI note number
     */
    public static String getNoteNameAsString(int midiNote) {
        String[] noteNames = NOTE_NAMES;
        int octave = (midiNote / 12) - 2;
        String note = noteNames[midiNote % 12];
        return note + octave;
    }

    /**
     * Converts a note name and octave into a MIDI note number.
     * The note name should be one of the standard note names (e.g., "C", "C#",
     * "D").
     * The octave is an integer representing the octave range, where 0 is the MIDI
     * octave 2.
     * 
     * @param noteName the name of the note (e.g., "C", "C#", "D")
     * @param octave   the octave number (e.g., 4 for "C4")
     * @return the MIDI note number corresponding to the given note name and octave
     */

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


    /**
     * Returns the duration of a note length as a double value.
     * This method takes a string representation of a note length and returns the
     * duration as a double value. The duration is in beats, where 1.0 is one beat.
     * The method supports note lengths from 1/128 to 1/2, including dotted and
     * triplet note lengths.
     *
     * @param selectedNoteLength the note length to convert to a duration
     * @return the duration of the given note length as a double value
     */
    public static double getNoteLengthAsDouble(String selectedNoteLength) {
        double duration;
        switch (selectedNoteLength) {
            // Straight (non-triplet, non-dotted)
            case "1/2":
                duration = 2.0;
                break;
            case "1/4":
                duration = 1.0;
                break;
            case "1/8":
                duration = 0.5;
                break;
            case "1/16":
                duration = 0.25;
                break;
            case "1/32":
                duration = 0.125;
                break;
            case "1/64":
                duration = 0.0625;
                break;
            case "1/128":
                duration = 0.03125;
                break;

            // Dotted notes (1.5 times the straight note)
            case "1/2.":
                duration = 2.0 * 1.5; // 3.0
                break;
            case "1/4.":
                duration = 1.0 * 1.5; // 1.5
                break;
            case "1/8.":
                duration = 0.5 * 1.5; // 0.75
                break;
            case "1/16.":
                duration = 0.25 * 1.5; // 0.375
                break;
            case "1/32.":
                duration = 0.125 * 1.5; // 0.1875
                break;
            case "1/64.":
                duration = 0.0625 * 1.5; // 0.09375
                break;
            case "1/128.":
                duration = 0.03125 * 1.5; // 0.046875
                break;

            // Triplet notes (2/3 times the straight note)
            case "1/2 - 3t":
                duration = 2.0 * (2.0 / 3.0); // ~1.3333
                break;
            case "1/4 - 3t":
                duration = 1.0 * (2.0 / 3.0); // ~0.6667
                break;
            case "1/8 - 3t":
                duration = 0.5 * (2.0 / 3.0); // ~0.3333
                break;
            case "1/16 - 3t":
                duration = 0.25 * (2.0 / 3.0); // ~0.1667
                break;
            case "1/32 - 3t":
                duration = 0.125 * (2.0 / 3.0); // ~0.0833
                break;
            case "1/64 - 3t":
                duration = 0.0625 * (2.0 / 3.0); // ~0.0417
                break;
            case "1/128 - 3t":
                duration = 0.03125 * (2.0 / 3.0); // ~0.0208
                break;

            // Default
            default:
                duration = 1.0;
                break;
        }

        return duration;
    }

}
