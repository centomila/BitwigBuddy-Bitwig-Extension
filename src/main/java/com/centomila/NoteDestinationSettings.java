package com.centomila;

import static com.centomila.utils.PopupUtils.*;
import static com.centomila.utils.SettingsHelper.*;
import com.bitwig.extension.controller.api.SettableRangedValue;
import com.bitwig.extension.controller.api.Setting;
import com.bitwig.extension.controller.api.Channel;
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.EnumValue;
import com.bitwig.extension.controller.api.SettableEnumValue;
import com.bitwig.extension.controller.api.PlayingNote;
import com.bitwig.extension.controller.api.PlayingNoteArrayValue;
import com.bitwig.extension.controller.api.DocumentState;

import java.util.Arrays;

/**
 * Manages the note destination settings including note and octave.
 */
public class NoteDestinationSettings {

   private String currentNoteAsString;
   private int currentOctaveAsInt;
   private final Setting noteChannelSetting;

   /**
    * Constructs a new NoteDestinationSettings instance.
    *
    * @param noteChannelSetting the setting for the note channel
    * @param initialNote        the initial note value as a string
    * @param initialOctave      the initial octave value as an integer
    */
   public NoteDestinationSettings(Setting noteChannelSetting, String initialNote, int initialOctave) {
      this.noteChannelSetting = noteChannelSetting;
      this.currentNoteAsString = initialNote;
      this.currentOctaveAsInt = initialOctave;
   }

   /**
    * Returns the current MIDI note number calculated from the note and octave.
    *
    * @return the MIDI note number as an integer
    */
   public int getCurrentNoteDestinationAsInt() {
      return Utils.getMIDINoteNumberFromStringAndOctave(currentNoteAsString, currentOctaveAsInt);
   }

   /**
    * Pops up a display showing the current note destination.
    */
   public void popupNoteDestination() {
      showPopup("Note Destination: " + currentNoteAsString + currentOctaveAsInt);
   }

   /**
    * Returns the current channel number based on the note channel setting.
    *
    * @return the channel number as an integer (0-indexed)
    */
   public int getCurrentChannelAsInt() {
      int channel = (int) Math.round(((SettableRangedValue) noteChannelSetting).getRaw());
      return channel - 1;
   }

   /**
    * Updates the current note with the provided value and displays the updated
    * note destination.
    *
    * @param note the new note value as a String
    */
   public void setCurrentNote(String note) {
      this.currentNoteAsString = note;
      getCurrentNoteDestinationAsInt();
      popupNoteDestination();
   }

   /**
    * Updates the current octave with the provided value and displays the updated
    * note destination.
    *
    * @param octave the new octave value as an integer
    */
   public void setCurrentOctave(int octave) {
      this.currentOctaveAsInt = octave;
      getCurrentNoteDestinationAsInt();
      popupNoteDestination();
   }

   /**
    * Initializes the note destination settings and registers observers.
    *
    * @param extension the BeatBuddy extension instance
    */
   public static void init(BeatBuddyExtension extension) {
      var documentState = extension.getDocumentState();
      ControllerHost host = extension.getHost();

      setupSpacerSetting(documentState);
      setupDestinationSettings(extension, documentState);
      setupNoteChannelSetting(extension, documentState);
      setupNoteDestSettings(extension);
      setupLearnNoteSetting(extension, documentState);
      setupPlayingNotesObserver(extension, host);
   }

   /**
    * Sets up a spacer setting for layout spacing that is always disabled.
    *
    * @param documentState the document state used to derive settings
    */
   private static void setupSpacerSetting(Object documentState) {
      // Add a spacer setting for layout spacing.
      Setting spacerNoteDestination = (Setting) ((DocumentState) documentState).getStringSetting(
            "NOTE DESTINATION---------------", "Note Destination", 0,
            "---------------------------------------------------");
      disableSetting(spacerNoteDestination); // Spacers are always disabled
   }

   /**
    * Sets up the destination settings for note and octave.
    *
    * @param extension     the BeatBuddy extension instance
    * @param documentState the document state used to derive settings
    */
   private static void setupDestinationSettings(BeatBuddyExtension extension, Object documentState) {
      String[] noteDestinationOptions = Utils.NOTE_NAMES;
      String[] octaveDestinationOptions = Arrays.stream(Utils.NOTE_OCTAVES)
            .mapToObj(String::valueOf)
            .toArray(String[]::new);

      extension.setNoteDestinationSetting((Setting) ((DocumentState) documentState).getEnumSetting(
            "Note Destination", "Note Destination", noteDestinationOptions, noteDestinationOptions[0]));
      extension.setNoteOctaveSetting((Setting) ((DocumentState) documentState).getEnumSetting(
            "Note Octave", "Note Destination", octaveDestinationOptions, octaveDestinationOptions[3]));
   }

   /**
    * Sets up the note channel setting.
    *
    * @param extension     the BeatBuddy extension instance
    * @param documentState the document state used to derive settings
    */
   private static void setupNoteChannelSetting(BeatBuddyExtension extension, Object documentState) {
      extension.setNoteChannelSetting((Setting) ((DocumentState) documentState).getNumberSetting(
            "Note Channel", "Note Destination", 1, 16, 1, "Channel MIDI", 1));
   }

   /**
    * Initializes the note destination settings and registers observers for
    * changes.
    *
    * @param extension the BeatBuddy extension instance
    */
   private static void setupNoteDestSettings(BeatBuddyExtension extension) {
      // Initialize using current settings from enum values.
      String initialNote = ((EnumValue) extension.noteDestinationSetting).get();
      int initialOctave = Integer.parseInt(((EnumValue) extension.noteOctaveSetting).get());
      extension.setNoteDestSettings(new NoteDestinationSettings(
            extension.noteChannelSetting, initialNote, initialOctave));

      // Register observer for note changes.
      ((EnumValue) extension.noteDestinationSetting).addValueObserver(newValue -> {
         ((NoteDestinationSettings) extension.noteDestSettings).setCurrentNote(newValue);
         forceNoteRangeMaxToG8(extension);
      });

      // Register observer for octave changes.
      ((EnumValue) extension.noteOctaveSetting).addValueObserver(newValue -> {
         ((NoteDestinationSettings) extension.noteDestSettings)
               .setCurrentOctave(Integer.parseInt(newValue));
         forceNoteRangeMaxToG8(extension);
      });
   }

   /**
    * Sets up the learn note setting.
    *
    * @param extension     the BeatBuddy extension instance
    * @param documentState the document state used to derive settings
    */
   private static void setupLearnNoteSetting(BeatBuddyExtension extension, Object documentState) {
      final String[] LEARN_NOTE_OPTIONS = new String[] { "On", "Off" };
      extension.learnNoteSetting = (Setting) ((DocumentState) documentState).getEnumSetting(
            "Learn Note", "Note Destination", LEARN_NOTE_OPTIONS, "Off");
   }

   /**
    * Sets up the observer for playing notes.
    *
    * @param extension the BeatBuddy extension instance
    * @param host      the controller host instance
    */
   private static void setupPlayingNotesObserver(BeatBuddyExtension extension, ControllerHost host) {
      Channel cursorChannel = host.createCursorTrack(0, 0);
      PlayingNoteArrayValue playingNotes = cursorChannel.playingNotes();
      playingNotes.markInterested();

      playingNotes.addValueObserver(notes -> {
         if (((EnumValue) extension.learnNoteSetting).get().equals("On")) {
            for (PlayingNote note : notes) {
               String noteName = getNoteNameFromKey(note.pitch());
               String[] keyAndOctave = getKeyAndOctaveFromNoteName(noteName);
               ((SettableEnumValue) extension.noteDestinationSetting).set(keyAndOctave[0]);
               ((SettableEnumValue) extension.noteOctaveSetting).set(keyAndOctave[1]);
            }
         }
      });

      ((EnumValue) extension.learnNoteSetting).addValueObserver(value -> {
         if (value.equals("On")) {
            playingNotes.subscribe();
         } else {
            playingNotes.unsubscribe();
         }
      });
   }

   /**
    * Converts a MIDI key value into its note name with octave. For example, 60
    * becomes "C4".
    *
    * @param key the MIDI key value
    * @return the note name as a String
    */
   public static String getNoteNameFromKey(int key) {
      String[] noteNames = { "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B" };
      int octave = (key / 12) - 2;
      int noteIndex = key % 12;
      return noteNames[noteIndex] + octave;
   }

   /**
    * Splits the note name into its note and octave components.
    *
    * @param noteName the combined note name (e.g., "C#4")
    * @return a String array where the first element is the note and the second
    *         element is the octave
    */
   public static String[] getKeyAndOctaveFromNoteName(String noteName) {
      String note;
      int octave;
      int octaveStartIndex = -1;
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
         octave = 0;
      }
      return new String[] { note, String.valueOf(octave) };
   }

   /**
    * Checks if the current octave is 8 with one of the notes G#, A, A#, or B.
    * If so, updates the note destination setting to "G".
    *
    * @param extension the BeatBuddy extension instance
    */
   private static void forceNoteRangeMaxToG8(BeatBuddyExtension extension) {
      String currentNote = ((EnumValue) extension.noteDestinationSetting).get();
      int currentOctave = Integer.parseInt(((EnumValue) extension.noteOctaveSetting).get());
      if (currentOctave == 8 &&
            (currentNote.equals("G#") || currentNote.equals("A") ||
             currentNote.equals("A#") || currentNote.equals("B"))) {
         ((SettableEnumValue) extension.noteDestinationSetting).set("G");
      }
   }
}
