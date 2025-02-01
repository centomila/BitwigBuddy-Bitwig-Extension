package com.centomila;

public interface DrumsNotes {

    void init();

    final Object[][] gmDrums = {
            { 35, "Acoustic Bass Drum", "B0" },
            { 36, "Bass Drum", "C1" },
            { 37, "Side Stick", "C#1" },
            { 38, "Acoustic Snare", "D1" },
            { 39, "Hand Clap", "D#1" },
            { 40, "Electric Snare", "E1" },
            { 41, "Low Floor Tom", "F1" },
            { 42, "Closed Hi-Hat", "F#1" },
            { 43, "High Floor Tom", "G1" },
            { 44, "Pedal Hi-Hat", "G#1" },
            { 45, "Low Tom", "A1" },
            { 46, "Open Hi-Hat", "A#1" },
            { 47, "Low-Mid Tom", "B1" },
            { 48, "Hi-Mid Tom", "C2" },
            { 49, "Crash Cymbal 1", "C#2" },
            { 50, "High Tom", "D2" },
            { 51, "Ride Cymbal 1", "D#2" },
            { 52, "Chinese Cymbal", "E2" },
            { 53, "Ride Bell", "F2" },
            { 54, "Tambourine", "F#2" },
            { 55, "Splash Cymbal", "G2" },
            { 56, "Cowbell", "G#2" },
            { 57, "Crash Cymbal 2", "A2" },
            { 58, "Vibraslap", "A#2" },
            { 59, "Ride Cymbal 2", "B2" },
            { 60, "Hi Bongo", "C3" },
            { 61, "Lo Bongo", "C#3" },
            { 62, "Mute Hi Conga", "D3" },
            { 63, "Open Hi Conga", "D#3" },
            { 64, "Low Conga", "E3" },
            { 65, "High Timbale", "F3" },
            { 66, "Low Timbale", "F#3" },
            { 67, "High Agogo", "G3" },
            { 68, "Low Agogo", "G#3" },
            { 69, "Cabasa", "A3" },
            { 70, "Maracas", "A#3" },
            { 71, "Short Whistle", "B3" },
            { 72, "Long Whistle", "C4" },
            { 73, "Short Guiro", "C#4" },
            { 74, "Long Guiro", "D4" },
            { 75, "Claves", "D#4" },
            { 76, "High Wood Block", "E4" },
            { 77, "Low Wood Block", "F4" },
            { 78, "Mute Cuica", "F#4" },
            { 79, "Open Cuica", "G4" },
            { 80, "Mute Triangle", "G#4" },
            { 81, "Open Triangle", "A4" }
    };
    

    // Example static method to look up the MIDI note (int) by drum name:
    static int getDrumNumberByName(String drumName) {
        for (Object[] row : gmDrums) {
            // row[0] is the int, row[1] is the name, row[2] is the note like "C1"
            if (row[1].equals(drumName)) {
                return (Integer) row[0];
            }
        }
        throw new IllegalArgumentException("No drum with name: " + drumName);
    }
}
