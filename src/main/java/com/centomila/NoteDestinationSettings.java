package com.centomila;

import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.SettableRangedValue;
import com.bitwig.extension.controller.api.Setting;
import com.bitwig.extension.controller.api.EnumValue;

import java.util.Arrays;

/**
 * Manages the note destination settings including note and octave.
 */
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
      // Refresh note destination and notify
      getCurrentNoteDestinationAsInt();
      notifyNoteDestination();
   }

   public void setCurrentOctave(int octave) {
      this.currentOctaveAsInt = octave;
      // Refresh note destination and notify
      getCurrentNoteDestinationAsInt();
      notifyNoteDestination();
   }

   /**
    * Initializes the note destination settings and registers observers.
    *
    * @param extension the BeatBuddy extension instance
    */
   public static void init(BeatBuddyExtension extension) {
      var documentState = extension.getDocumentState();
      var host = extension.getHost();

      // Initialize available options.
      String[] noteDestinationOptions = Utils.NOTE_NAMES;
      String[] octaveDestinationOptions = Arrays.stream(Utils.NOTE_OCTAVES)
            .mapToObj(String::valueOf)
            .toArray(String[]::new);

      // Set up Note Destination setting.
      extension.setNoteDestinationSetting((Setting) documentState.getEnumSetting(
            "Note Destination", "Note Destination",
            noteDestinationOptions, noteDestinationOptions[0]
      ));

      // Set up Note Octave setting.
      extension.setNoteOctaveSetting((Setting) documentState.getEnumSetting(
            "Note Octave", "Note Destination",
            octaveDestinationOptions, octaveDestinationOptions[3]
      ));

      // Set up Note Channel setting.
      extension.setNoteChannelSetting((Setting) documentState.getNumberSetting(
            "Note Channel", "Note Destination",
            1, 16, 1, "Channel MIDI", 1
      ));

      // Create and assign NoteDestinationSettings.
      extension.setNoteDestSettings(new NoteDestinationSettings(host, 
            extension.getNoteChannelSetting(),
            noteDestinationOptions[0],
            3
      ));

      // Register observer for Note Destination changes.
      ((EnumValue) extension.getNoteDestinationSetting()).addValueObserver(newValue -> {
         extension.getNoteDestSettings().setCurrentNote(newValue);
      });

      // Register observer for Note Octave changes.
      ((EnumValue) extension.getNoteOctaveSetting()).addValueObserver(newValue -> {
         extension.getNoteDestSettings().setCurrentOctave(Integer.parseInt(newValue));
      });

      // Add a spacer setting for layout spacing.
      extension.setSpacer2((Setting) documentState.getStringSetting(
            "----", "Clip", 0, "---------------------------------------------------"
      ));
      extension.getSpacer2().disable();
   }
}
