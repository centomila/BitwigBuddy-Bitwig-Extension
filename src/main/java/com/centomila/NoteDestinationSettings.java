package com.centomila;

import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.SettableRangedValue;
import com.bitwig.extension.controller.api.Setting;

public class NoteDestinationSettings {
    private final ControllerHost host;
    private String currentNoteAsString;
    private int currentOctaveAsInt;
    private final Setting noteChannelSetting;

    public NoteDestinationSettings(ControllerHost host, Setting noteChannelSetting, String initialNote, int initialOctave) {
        this.host = host;
        this.noteChannelSetting = noteChannelSetting;
        this.currentNoteAsString = initialNote;
        this.currentOctaveAsInt = initialOctave;
    }

    public int getCurrentNoteDestinationAsInt() {
        return Utils.getMIDINoteNumberFromStringAndOctave(currentNoteAsString, currentOctaveAsInt);
    }

    public void notifyNoteDestination() {
        host.showPopupNotification("Note Destination: " + currentNoteAsString + currentOctaveAsInt);
    }

    public int getCurrentChannelAsInt() {
        int channel = (int) Math.round(((SettableRangedValue) noteChannelSetting).getRaw());
        return channel - 1;
    }

    public void setCurrentNote(String note) {
        this.currentNoteAsString = note;
        getCurrentNoteDestinationAsInt();
        notifyNoteDestination();
    }

    public void setCurrentOctave(int octave) {
        this.currentOctaveAsInt = octave;
        getCurrentNoteDestinationAsInt();
        notifyNoteDestination();
    }
}
