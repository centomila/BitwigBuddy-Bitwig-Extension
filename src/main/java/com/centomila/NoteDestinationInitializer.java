package com.centomila;

import java.util.Arrays;
import com.bitwig.extension.controller.api.Setting;
import com.bitwig.extension.controller.api.EnumValue;

public class NoteDestinationInitializer {
    public static void initNoteDestinationSetting(BeatBuddyExtension extension) {
        // Retrieve document state from extension
        var documentState = extension.getDocumentState();
        var host = extension.getHost();

        // Note destination dropdown
        String[] NOTEDESTINATION_OPTIONS = Utils.NOTE_NAMES;
        extension.setNoteDestinationSetting((Setting) documentState.getEnumSetting("Note Destination", "Note Destination",
                NOTEDESTINATION_OPTIONS, NOTEDESTINATION_OPTIONS[0]));

        // Note OCT destination dropdown
        String[] OCTAVEDESTINATION_OPTIONS = Arrays.stream(Utils.NOTE_OCTAVES)
                .mapToObj(String::valueOf)
                .toArray(String[]::new);
        extension.setNoteOctaveSetting((Setting) documentState.getEnumSetting("Note Octave", "Note Destination",
                OCTAVEDESTINATION_OPTIONS, OCTAVEDESTINATION_OPTIONS[3]));

        extension.setNoteChannelSetting((Setting) documentState.getNumberSetting("Note Channel", "Note Destination",
                1, 16, 1, "Channel MIDI", 1));

        // Initialize NoteDestinationSettings and set in extension
        extension.setNoteDestSettings(new NoteDestinationSettings(host, extension.getNoteChannelSetting(),
                NOTEDESTINATION_OPTIONS[0], 3));

        ((EnumValue) extension.getNoteDestinationSetting()).addValueObserver(newValue -> {
            extension.getNoteDestSettings().setCurrentNote(newValue);
        });

        ((EnumValue) extension.getNoteOctaveSetting()).addValueObserver(newValue -> {
            extension.getNoteDestSettings().setCurrentOctave(Integer.parseInt(newValue));
        });

        // Empty string for spacing
        extension.setSpacer2((Setting) documentState.getStringSetting("----", "Clip", 0,
                "---------------------------------------------------"));
        extension.getSpacer2().disable();
    }
}
