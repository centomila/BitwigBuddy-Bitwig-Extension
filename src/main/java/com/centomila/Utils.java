package com.centomila;

import java.util.Arrays;

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

    public static String[] STEPSIZE_OPTIONS = new String[] {
            "1/1", "1/2", "1/4", "1/8", "1/16", "1/32", "1/64", "1/128"
    };

    public static String[] STEPSIZE_CATEGORY_OPTIONS = new String[] {
            "Straight", ".", "3t", "5t", "7t"
    };

    /**
     * Converts a note length and subdivision into a double representing the duration.
     * 
     * The method takes a note length in string format and a subdivision to determine
     * the duration of a note in terms of beats. The note length must be a valid entry
     * from the predefined STEPSIZE_OPTIONS array, and the subdivision can be one of
     * the standard musical subdivisions ("." for dotted, "3t" for triplet, "5t" for
     * quintuplet, "7t" for septuplet). The method calculates the duration using
     * these values.
     * 
     * @param selectedNoteLength    the selected note length as a string (e.g., "1/4", "1/8")
     * @param selectedSubdivision   the selected subdivision as a string (e.g., ".", "3t")
     * @return                      the calculated duration of the note as a double
     * @throws IllegalArgumentException if the selectedNoteLength is invalid
     */

    public static double getNoteLengthAsDouble(String selectedNoteLength, String selectedSubdivision) {
        double duration = 0.0;

        double noteLengths[] = { 4.0, 2.0, 1.0, 0.5, 0.25, 0.125, 0.0625, 0.03125 };

        int index = Arrays.asList(STEPSIZE_OPTIONS).indexOf(selectedNoteLength);
        if (index == -1) {
            throw new IllegalArgumentException("Invalid note length: " + selectedNoteLength);
        }

        double multiplier = 1.0;
        switch (selectedSubdivision) {
            case ".":
                multiplier = 1.5;
                break;
            case "3t":
                multiplier = 2.0 / 3.0;
                break;
            case "5t":
                multiplier = 4.0 / 5.0;
                break;
            case "7t":
                multiplier = 4.0 / 7.0;
                break;
            default:
                break;
        }

        duration = noteLengths[index] * multiplier;

        return duration;
    }

    /**
     * Compares two strings in natural order so that numeric parts are compared numerically.
     */
    public static int naturalCompare(String a, String b) {
        int ia = 0, ib = 0;
        char ca, cb;
        while (ia < a.length() && ib < b.length()) {
            ca = a.charAt(ia);
            cb = b.charAt(ib);

            if (Character.isDigit(ca) && Character.isDigit(cb)) {
                // Extract full numeric substrings.
                int startIa = ia;
                int startIb = ib;
                while (ia < a.length() && Character.isDigit(a.charAt(ia))) ia++;
                while (ib < b.length() && Character.isDigit(b.charAt(ib))) ib++;
                String numA = a.substring(startIa, ia);
                String numB = b.substring(startIb, ib);
                try {
                    int intA = Integer.parseInt(numA);
                    int intB = Integer.parseInt(numB);
                    if (intA != intB) {
                        return intA - intB;
                    }
                } catch (NumberFormatException e) {
                    int cmp = numA.compareTo(numB);
                    if (cmp != 0) {
                        return cmp;
                    }
                }
                continue;
            }

            if (ca != cb) {
                return ca - cb;
            }
            ia++;
            ib++;
        }
        return a.length() - b.length();
    }
}

