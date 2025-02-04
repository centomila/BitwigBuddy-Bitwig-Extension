package com.centomila;

import java.util.Arrays;
import java.util.Random;

// import com.bitwig.extension.controller.api.Action;

import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.ControllerExtension;
import com.bitwig.extension.controller.api.Application;
import com.bitwig.extension.controller.api.Clip;
// import com.bitwig.extension.controller.api.Cursor;
// import com.bitwig.extension.controller.api.Track;
import com.bitwig.extension.controller.api.DocumentState;
import com.bitwig.extension.controller.api.EnumValue;
import com.bitwig.extension.controller.api.SettableEnumValue;
import com.bitwig.extension.controller.api.SettableRangedValue;
// import com.bitwig.extension.controller.api.SettableIntegerValue;
// import com.bitwig.extension.controller.api.SettableBooleanValue;
// import com.bitwig.extension.controller.api.SettableStringArrayValue;
// import com.bitwig.extension.controller.api.BooleanValue;
import com.bitwig.extension.controller.api.SettableBeatTimeValue;

import com.bitwig.extension.controller.api.Setting;
import com.bitwig.extension.controller.api.Signal;

public class BeatBuddyExtension extends ControllerExtension {
   // private Application application;
   private Clip cursorClip;
   private Clip arrangerClip;
   // private Track track;
   private DocumentState documentState;
   private Setting patternSelectorSetting;
   private Setting noteLengthSetting; // How long each note should be
   private Setting stepSizSetting;
   private Setting noteDestinationSetting;
   private Setting noteOctaveSetting;
   private Setting noteChannelSetting;
   private Setting toggleLauncherArrangerSetting;
   private Setting autoResizeLoopLengthSetting;
   private String currentNoteAsString;
   private int currentOctaveAsInt;

   private Setting spacerSetting;

   protected BeatBuddyExtension(final BeatBuddyExtensionDefinition definition, final ControllerHost host) {
      super(definition, host);
   }

   @Override
   public void init() {
      final ControllerHost host = getHost();
      // Initialize API objects
      // application = host.createApplication();
      cursorClip = host.createLauncherCursorClip((16 * 8), 128);
      arrangerClip = host.createArrangerCursorClip((16 * 8), 128);
      documentState = host.getDocumentState();

      // Generate button
      Signal generateButton = documentState.getSignalSetting("Generate!", "Generate", "Generate!");

      generateButton.addSignalObserver(() -> {
         generateDrumPattern();
      });

      // Define pattern settings
      final String[] PATTERN_OPTIONS = Arrays.stream(DrumPatterns.patterns)
            .map(pattern -> pattern[0].toString())
            .toArray(String[]::new);
      patternSelectorSetting = (Setting) documentState.getEnumSetting("Pattern", "Generate", PATTERN_OPTIONS,
            "Random");

      // Pattern destination dropdown
      String[] NOTEDESTINATION_OPTIONS = Utils.NOTE_NAMES;
      noteDestinationSetting = (Setting) documentState.getEnumSetting("Note Destination", "Clip",
            NOTEDESTINATION_OPTIONS,
            NOTEDESTINATION_OPTIONS[0]);

      ((EnumValue) noteDestinationSetting).addValueObserver(newValue -> {
         currentNoteAsString = newValue;
         getCurrentNoteDestinationAsInt();
      });

      // Pattern destination dropdown
      String[] OCTAVEDESTINATION_OPTIONS = Arrays.stream(Utils.NOTE_OCTAVES)
            .mapToObj(String::valueOf)
            .toArray(String[]::new);
      noteOctaveSetting = (Setting) documentState.getEnumSetting("Note Octave", "Clip",
            OCTAVEDESTINATION_OPTIONS,
            OCTAVEDESTINATION_OPTIONS[3]);

      ((EnumValue) noteOctaveSetting).addValueObserver(newValue -> {
         currentOctaveAsInt = Integer.parseInt(newValue);
         getCurrentNoteDestinationAsInt();
      });

      // Pattern step size
      final String[] STEPSIZE_OPTIONS = new String[] {
            "Straight", "1/2", "1/4", "1/8", "1/16", "1/32", "1/64", "1/128",
            "Dotted", "1/2.", "1/4.", "1/8.", "1/16.", "1/32.", "1/64.", "1/128.",
            "Triplets", "1/2 - 3t", "1/4 - 3t", "1/8 - 3t", "1/16 - 3t", "1/32 - 3t", "1/64 - 3t", "1/128 - 3t"
      };
      stepSizSetting = (Setting) documentState.getEnumSetting("Step Size", "Clip", STEPSIZE_OPTIONS, "1/16");

      // set the note length equal to the selected step size
      ((EnumValue) stepSizSetting).addValueObserver(newValue -> {
         switch (newValue) {
            case "Straight":
               newValue = "1/16";
               break;
            case "Dotted":
               newValue = "1/16.";
               break;
            case "Triplets":
               newValue = "1/16 - 3t";
               break;
         }
         // Set both note length and step size
         ((SettableEnumValue) stepSizSetting).set(newValue);
         ((SettableEnumValue) noteLengthSetting).set(newValue);
      });

      // Pattern note length
      noteLengthSetting = (Setting) documentState.getEnumSetting("Note Length", "Clip", STEPSIZE_OPTIONS, "1/16");

      ((EnumValue) noteLengthSetting).addValueObserver(newValue -> {
         switch (newValue) {
            case "Straight":
               newValue = "1/16";
               break;
            case "Dotted":
               newValue = "1/16.";
               break;
            case "Triplets":
               newValue = "1/16 - 3t";
               break;
         }

         ((SettableEnumValue) noteLengthSetting).set(newValue);
      });

      noteChannelSetting = (Setting) documentState.getNumberSetting("Note Channel", "Clip", 1, 16, 1, "", 1);

      autoResizeLoopLengthSetting = (Setting) documentState.getEnumSetting("Auto resize loop length", "Clip",
            new String[] { "On", "Off" }, "On");

      // Empty string for spacing
      spacerSetting = (Setting) documentState.getStringSetting("----", "Z", 0,
            "---------------------------------------------------");
      spacerSetting.disable();

      // Clear current clip
      documentState.getSignalSetting("Clear current clip", "Z", "Clear current clip").addSignalObserver(() -> {
         getLauncherArrangerAsClip().clearSteps();
      });

      // Initialize launcher/arranger toggle
      initToggleLauncherArrangerSetting();

      // Show a notification to confirm initialization
      host.showPopupNotification("BeatBuddy Initialized");

   }

   private void initToggleLauncherArrangerSetting() {
      // Launcher/Arranger toggle
      final String[] TOGGLE_LAUNCHER_ARRANGER_OPTIONS = new String[] { "Launcher", "Arranger", };
      toggleLauncherArrangerSetting = (Setting) documentState.getEnumSetting("Destination Launcher/Arranger", "Z",
            TOGGLE_LAUNCHER_ARRANGER_OPTIONS,
            TOGGLE_LAUNCHER_ARRANGER_OPTIONS[0]);
   }

   /**
    * Returns the MIDI note value of the currently selected note destination.
    *
    * The note destination is determined based on user settings, which can be one
    * of the following:
    * "Kick", "Snare", "Hi-Hat Closed", "Hi-Hat Open", "Cymbal", "Tom 1", "Tom 2",
    * "Tom 3", "Tom 4",
    * "Percussion 1", "Percussion 2", "Percussion 3", or "Percussion 4".
    * 
    * If an unrecognized note type is selected, a notification is displayed.
    *
    * @return The MIDI note value for the selected note destination.
    */

   private int getCurrentNoteDestinationAsInt() {
      getHost().showPopupNotification("Selected Note: " + currentNoteAsString + currentOctaveAsInt);
      int currentValueInt = Utils.getMIDINoteNumberFromStringAndOctave(currentNoteAsString, currentOctaveAsInt);
      return currentValueInt;
   }

   /**
    * Returns the currently selected channel as an integer, 0-indexed.
    *
    * @return The currently selected channel as an integer, 0-indexed.
    */
   private int getCurrentChannelAsDouble() {
      int channel = (int) Math.round(((SettableRangedValue) noteChannelSetting).getRaw());
      return channel - 1;
   }

   /**
    * Returns the Clip object for either the Arranger Clip Launcher or the Launcher
    * Clip depending on the value of the "Launcher/Arranger" setting.
    * 
    * @return A Clip object, either the Arranger Clip Launcher or the Launcher
    *         Clip.
    */
   private Clip getLauncherArrangerAsClip() {
      String launcherArrangerSelection = ((EnumValue) toggleLauncherArrangerSetting).get();
      return launcherArrangerSelection.equals("Arranger") ? arrangerClip : cursorClip;
   }

   /**
    * Generates a drum pattern based on user-selected settings and applies it to
    * the current clip.
    *
    * This method utilizes various settings, such as note length, step size, and
    * pattern selection,
    * to generate and apply a drum pattern to the active clip in the Bitwig Studio
    * environment.
    * If a random pattern is selected, a randomized pattern is created.
    *
    * It also provides functionality to automatically resize the loop length of the
    * clip
    * to fit the length of the generated pattern if the auto-resize option is
    * enabled.
    *
    * The steps are set for a specific channel and note destination within the
    * clip,
    * and the current step contents are selected for further manipulation.
    */
   private void generateDrumPattern() {
      Clip clip = getLauncherArrangerAsClip();
      // if the clip doesn't exist, create it
      // track.createNewLauncherClip(0, 1);

      String selectedNoteLength = ((EnumValue) noteLengthSetting).get(); // Get the current selected value of noteLength
      double durationValue = Utils.getNoteLengthAsDouble(selectedNoteLength);

      String selectedStepSize = ((EnumValue) stepSizSetting).get(); // Get the current selected value of stepSize
      double stepSize = Utils.getNoteLengthAsDouble(selectedStepSize);
      clip.setStepSize(stepSize);

      int channel = getCurrentChannelAsDouble();
      int y = getCurrentNoteDestinationAsInt();

      clip.clearStepsAtY(channel, y);

      String selectedPattern = ((EnumValue) patternSelectorSetting).get();
      int[] currentPattern = DrumPatterns.getPatternByName(selectedPattern);

      // if selected pattern = "random", generate a random pattern
      if (selectedPattern.equals("Random")) {
         Random random = new Random();
         for (int i = 0; i < 16; i++) {
            currentPattern[i] = random.nextInt(128);
            // randomly change the value to 0
            if (random.nextInt(4) == 0) {
               currentPattern[i] = 0;
            }
         }
      }

      for (int i = 0; i < currentPattern.length; i++) {
         if (currentPattern[i] > 0) {
            clip.setStep(channel, i, y, currentPattern[i], durationValue);
         }
      }

      // if autoResizeLoopLength is "On", resize the loop length to fit the pattern
      if (((EnumValue) autoResizeLoopLengthSetting).get().equals("On")) {
         // Calculate the beat length of the pattern
         double beatLength = stepSize * currentPattern.length;
         double loopStart = 0.0;
         double loopEnd = loopStart + beatLength;
         setLoopLength(loopStart, loopEnd);
      }

      clip.selectStepContents(channel, y, false);
      // application.zoomToFit();

   }

   /**
    * Sets the loop length of the currently selected clip to a given start and
    * end time in beats. Additionally sets the playback start and end times to
    * the same values.
    * 
    * @param loopStart the desired start time of the loop in beats
    * @param loopEnd   the desired end time of the loop in beats
    */
   private void setLoopLength(Double loopStart, Double loopEnd) {
      Clip clip = getLauncherArrangerAsClip();

      // These access the SettableRangedValue objects for loop start and loop length.
      SettableBeatTimeValue clipLoopStart = clip.getLoopStart();
      SettableBeatTimeValue clipLoopEnd = clip.getLoopLength();
      SettableBeatTimeValue playbackStart = clip.getPlayStart();
      SettableBeatTimeValue playbackEnd = clip.getPlayStop();

      // Set loop to start at 0.0 beats
      clipLoopStart.set(loopStart);
      clipLoopEnd.set(loopEnd);

      playbackStart.set(loopStart);
      playbackEnd.set(loopEnd);
   }

   @Override
   public void exit() {
      // Cleanup on exit
      getHost().showPopupNotification("BeatBuddy Exited");
   }

   @Override
   public void flush() {
      // Update logic if necessary
   }
}
