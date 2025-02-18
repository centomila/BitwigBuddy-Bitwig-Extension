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
 * Manages MIDI note destination settings for the BeatBuddy extension.
 * This class handles the configuration and updates of note values, octaves,
 * and MIDI channel settings, providing functionality for note learning and
 * value constraints.
 */
public class NoteDestinationSettings {
   private static String CATEGORY_NOTE_DESTINATION = "Note Destination";

   private String currentNoteAsString;
   private int currentOctaveAsInt;
   private final Setting noteChannelSetting;

   /**
    * Constructs a new NoteDestinationSettings instance with initial values.
    * 
    * @param noteChannelSetting The Setting object controlling the MIDI channel
    * @param initialNote The initial note value (e.g., "C", "F#")
    * @param initialOctave The initial octave value (-2 to 8)
    */
   public NoteDestinationSettings(Setting noteChannelSetting, String initialNote, int initialOctave) {
      this.noteChannelSetting = noteChannelSetting;
      this.currentNoteAsString = initialNote;
      this.currentOctaveAsInt = initialOctave;
   }

   /**
    * Calculates and returns the current MIDI note number.
    * Combines the current note and octave settings to determine the MIDI note number.
    * 
    * @return The MIDI note number (0-127)
    */
   public int getCurrentNoteDestinationAsInt() {
      return Utils.getMIDINoteNumberFromStringAndOctave(currentNoteAsString, currentOctaveAsInt);
   }

   /**
    * Displays a popup notification showing the current note destination.
    * The popup shows the note name concatenated with the octave number.
    */
   public void popupNoteDestination() {
      showPopup("Note Destination: " + currentNoteAsString + currentOctaveAsInt);
   }

   /**
    * Retrieves the current MIDI channel number from settings.
    * 
    * @return The MIDI channel number (0-15, zero-indexed)
    */
   public int getCurrentChannelAsInt() {
      int channel = (int) Math.round(((SettableRangedValue) noteChannelSetting).getRaw());
      return channel - 1;
   }

   /**
    * Updates the current note value and triggers a display update.
    * 
    * @param note The new note value (e.g., "C", "F#")
    */
   public void setCurrentNote(String note) {
      this.currentNoteAsString = note;
      getCurrentNoteDestinationAsInt();
      popupNoteDestination();
   }

   /**
    * Updates the current octave value and triggers a display update.
    * 
    * @param octave The new octave value (-2 to 8)
    */
   public void setCurrentOctave(int octave) {
      this.currentOctaveAsInt = octave;
      getCurrentNoteDestinationAsInt();
      popupNoteDestination();
   }

   /**
    * Initializes all note destination settings and observers.
    * Sets up note selection, octave selection, MIDI channel, and note learning functionality.
    * 
    * @param extension The BeatBuddy extension instance containing the settings
    */
   public static void init(BeatBuddyExtension extension) {
      ControllerHost host = extension.getHost();

      // Setup spacer
      Setting spacerNoteDestination = (Setting) createStringSetting(
            "NOTE DESTINATION---------------",
            CATEGORY_NOTE_DESTINATION,
            0,
            "---------------------------------------------------");

      disableSetting(spacerNoteDestination);

      // Setup note and octave destination settings
      String[] noteDestinationOptions = Utils.NOTE_NAMES;
      String[] octaveDestinationOptions = Arrays.stream(Utils.NOTE_OCTAVES)
            .mapToObj(i -> String.valueOf(i))
            .toArray(String[]::new);

      extension.noteDestinationSetting = (Setting) createEnumSetting(
            "Note Destination",
            "Note Destination",
            noteDestinationOptions,
            noteDestinationOptions[0]);
      extension.noteOctaveSetting = (Setting) createEnumSetting(
            "Note Octave",
            "Note Destination",
            octaveDestinationOptions,
            octaveDestinationOptions[3]);

      // Setup note channel setting
      extension.noteChannelSetting = (Setting) createNumberSetting(
            "Note Channel",
            "Note Destination",
            1,
            16,
            1,
            "MIDI Channel",
            1);

      String initialNote = ((EnumValue) extension.noteDestinationSetting).get();
      int initialOctave = Integer.parseInt(((EnumValue) extension.noteOctaveSetting).get());

      extension.setNoteDestSettings(new NoteDestinationSettings(
            extension.noteChannelSetting, initialNote, initialOctave));

      // Setup learn note setting
      final String[] LEARN_NOTE_OPTIONS = new String[] { "On", "Off" };

      extension.learnNoteSetting = (Setting) createEnumSetting(
            "Learn Note", "Note Destination", LEARN_NOTE_OPTIONS, "Off");

      // Setup note destination observers
      setupNoteDestinationObservers(extension);
      // Setup playing notes observer
      setupPlayingNotesObserver(extension, host);
   }

   /**
    * Configures value observers for note and octave destination changes.
    * Updates the current note/octave values and enforces the G8 maximum note constraint.
    * 
    * @param extension The BeatBuddy extension instance
    */
   private static void setupNoteDestinationObservers(BeatBuddyExtension extension) {
      // Register observer for note changes
      ((EnumValue) extension.noteDestinationSetting).addValueObserver(newValue -> {
         ((NoteDestinationSettings) extension.noteDestSettings).setCurrentNote(newValue);
         forceNoteRangeMaxToG8(extension);
      });

      // Register observer for octave changes
      ((EnumValue) extension.noteOctaveSetting).addValueObserver(newValue -> {
         ((NoteDestinationSettings) extension.noteDestSettings)
               .setCurrentOctave(Integer.parseInt(newValue));
         forceNoteRangeMaxToG8(extension);
      });
   }

   /**
    * Sets up note learning functionality by observing played notes.
    * When enabled, automatically updates note destination settings based on played notes.
    * 
    * @param extension The BeatBuddy extension instance
    * @param host The Bitwig Studio controller host
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
    * Converts a MIDI note number to its corresponding note name with octave.
    * 
    * @param key The MIDI note number (0-127)
    * @return The note name with octave (e.g., "C4", "F#3")
    */
   public static String getNoteNameFromKey(int key) {
      String[] noteNames = { "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B" };
      int octave = (key / 12) - 2;
      int noteIndex = key % 12;
      return noteNames[noteIndex] + octave;
   }

   /**
    * Separates a combined note name into its note and octave components.
    * 
    * @param noteName The combined note name (e.g., "C#4", "F-2")
    * @return A String array where [0] is the note name and [1] is the octave number
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
    * Enforces the maximum playable note constraint of G8.
    * If the current note is above G8, forces the note value back to G.
    * 
    * @param extension The BeatBuddy extension instance
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
