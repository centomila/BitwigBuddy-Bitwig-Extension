package com.centomila;

import com.centomila.utils.PopupUtils;
import com.bitwig.extension.controller.api.SettableRangedValue;
import com.bitwig.extension.controller.api.Setting;
import com.bitwig.extension.controller.api.Channel;
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.EnumValue;
import com.bitwig.extension.controller.api.SettableEnumValue;
import com.bitwig.extension.controller.api.PlayingNote;
import com.bitwig.extension.controller.api.PlayingNoteArrayValue;

import java.util.Arrays;

/**
 * Manages the note destination settings including note and octave.
 */
public class NoteDestinationSettings {

   private String currentNoteAsString;
   private int currentOctaveAsInt;
   private final Setting noteChannelSetting;

   public NoteDestinationSettings(Setting noteChannelSetting, String initialNote, int initialOctave) {

      this.noteChannelSetting = noteChannelSetting;
      this.currentNoteAsString = initialNote;
      this.currentOctaveAsInt = initialOctave;
   }

   public int getCurrentNoteDestinationAsInt() {
      return Utils.getMIDINoteNumberFromStringAndOctave(currentNoteAsString, currentOctaveAsInt);
   }

   public void notifyNoteDestination() {
      PopupUtils.showPopup("Note Destination: " + currentNoteAsString + currentOctaveAsInt);
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
      ControllerHost host = extension.getHost();

      // Add a spacer setting for layout spacing.
      Setting spacerNoteDestination = (Setting) documentState.getStringSetting("NOTE DESTINATION---------------",
            "Note Destination", 0,
            "---------------------------------------------------");
      spacerNoteDestination.disable();

      // Initialize available options.
      String[] noteDestinationOptions = Utils.NOTE_NAMES;
      String[] octaveDestinationOptions = Arrays.stream(Utils.NOTE_OCTAVES)
            .mapToObj(String::valueOf)
            .toArray(String[]::new);

      // Set up Note Destination setting.
      extension.setNoteDestinationSetting((Setting) documentState.getEnumSetting(
            "Note Destination", "Note Destination",
            noteDestinationOptions, noteDestinationOptions[0]));

      // Set up Note Octave setting.
      extension.setNoteOctaveSetting((Setting) documentState.getEnumSetting(
            "Note Octave", "Note Destination",
            octaveDestinationOptions, octaveDestinationOptions[3]));

      // Set up Note Channel setting.
      extension.setNoteChannelSetting((Setting) documentState.getNumberSetting(
            "Note Channel", "Note Destination",
            1, 16, 1, "Channel MIDI", 1));

      // Create and assign NoteDestinationSettings.
      extension.setNoteDestSettings(new NoteDestinationSettings(
            extension.noteChannelSetting,
            noteDestinationOptions[0],
            3));

      // Add a new enumvalue setting called Learn Note. Options are "On" and "Off"
      final String[] LEARN_NOTE_OPTIONS = new String[] { "On", "Off" };
      extension.learnNoteSetting = (Setting) documentState.getEnumSetting("Learn Note", "Note Destination", LEARN_NOTE_OPTIONS,
            "Off");

      // Playing notes observer

      // Get cursor channel
      Channel cursorChannel = host.createCursorTrack(0, 0);

      // Get playing notes value
      PlayingNoteArrayValue playingNotes = cursorChannel.playingNotes();

      // Mark interested and add observer during initialization
      playingNotes.markInterested();
      playingNotes.addValueObserver(notes -> {
         // Only show popup if Learn Note is "On"
         if (((EnumValue) extension.learnNoteSetting).get().equals("On")) {
            for (PlayingNote note : notes) {
               String noteName = getNoteNameFromKey(note.pitch());
               // PopupUtils.showPopup("Note played: " + noteName + " (velocity: " + Math.round(note.velocity()) + ")");
               
               ((SettableEnumValue) extension.noteDestinationSetting).set(getKeyAndOctaveFromNoteName(noteName)[0]);
               ((SettableEnumValue) extension.noteOctaveSetting).set(getKeyAndOctaveFromNoteName(noteName)[1]);
            }
         }
      });

      // Handle subscription based on setting value
      ((EnumValue) extension.learnNoteSetting).addValueObserver(value -> {
         if (value.equals("On")) {
            playingNotes.subscribe();
         } else {
            playingNotes.unsubscribe();
         }
      });

      // Register observer for Note Destination changes.
      ((EnumValue) extension.noteDestinationSetting).addValueObserver(newValue -> {
         ((NoteDestinationSettings) extension.noteDestSettings).setCurrentNote(newValue);
      });

      // Register observer for Note Octave changes.
      ((EnumValue) extension.noteOctaveSetting).addValueObserver(newValue -> {
         ((NoteDestinationSettings) extension.noteDestSettings).setCurrentOctave(Integer.parseInt(newValue));
      });


      }

   public static String getNoteNameFromKey(int key) {
      String[] noteNames = { "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B" };
      int octave = (key / 12) - 2; // Changed from -1 to -2
      int noteIndex = key % 12;
      // This can be from -C2 TO G8
      return noteNames[noteIndex] + octave;
   }

   public static String[] getKeyAndOctaveFromNoteName(String noteName) {
      // String noteName can be from C-2 TO G8

      // Get note name and octave
      String note;
      int octave;

      int octaveStartIndex = -1;
      // Find where the octave number starts (including possible negative sign)
      for (int i = 0; i < noteName.length(); i++) {
         char c = noteName.charAt(i);
         if (c == '-' || Character.isDigit(c)) {
            octaveStartIndex = i;
            break;
         }
      }

      if (octaveStartIndex != -1) {
         note = noteName.substring(0, octaveStartIndex);
         octave = Integer.parseInt(noteName.substring(octaveStartIndex));
      } else {
         note = noteName;
         octave = 0; // Default octave if no number found
      }

      // PopupUtils.showPopup("Note: " + note + " Octave: " + octave);
      return new String[] { note, String.valueOf(octave) };

   }
}
