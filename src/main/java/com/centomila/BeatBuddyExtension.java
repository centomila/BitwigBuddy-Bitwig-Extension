package com.centomila;

import java.util.Arrays;
import java.util.Random;

import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.ControllerExtension;
import com.bitwig.extension.controller.api.Application;
import com.bitwig.extension.controller.api.Clip;
import com.bitwig.extension.controller.api.DocumentState;
import com.bitwig.extension.controller.api.EnumValue;
import com.bitwig.extension.controller.api.SettableRangedValue;
import com.bitwig.extension.controller.api.Setting;

public class BeatBuddyExtension extends ControllerExtension implements DrumsNotes {
   private Application application;
   private Clip cursorClip;
   private Clip arrangerClip;
   private DocumentState documentState;
   private Setting patternSelectorSetting;
   private Setting noteLengthSetting; // How long each note should be
   private Setting stepSizSetting;
   private Setting noteDestinationSetting;
   private Setting noteChannelSetting;
   private Setting toggleLauncherArrangerSetting;
   // settings for separate notes
   private String noteUnitSetting = "MIDI Note";
   private Setting toggleNoteFields;
   private Setting noteKickSetting;
   private Setting noteSnareSetting;
   private Setting noteRimshotSetting;
   private Setting noteClosedHiHatSetting;
   private Setting noteOpenHiHatSetting;
   private Setting noteCymbalSetting;
   private Setting noteTom1Setting;
   private Setting noteTom2Setting;
   private Setting noteTom3Setting;
   private Setting noteTom4Setting;
   private Setting notePerc1Setting;
   private Setting notePerc2Setting;
   private Setting notePerc3Setting;
   private Setting notePerc4Setting;

   private Setting spacerSetting;

   protected BeatBuddyExtension(final BeatBuddyExtensionDefinition definition, final ControllerHost host) {
      super(definition, host);
   }

   @Override
   public void init() {
      final ControllerHost host = getHost();
      // Initialize API objects
      application = host.createApplication();
      cursorClip = host.createLauncherCursorClip((16 * 8), 128);
      arrangerClip = host.createArrangerCursorClip((16 * 8), 128);
      documentState = host.getDocumentState();

      // Generate button
      documentState.getSignalSetting("Generate!", "Generate", "Generate!").addSignalObserver(() -> {
         generateDrumPattern();
      });



      // Define pattern settings
      final String[] PATTERN_OPTIONS = Arrays.stream(DrumPatterns.patterns)
            .map(pattern -> pattern[0].toString())
            .toArray(String[]::new);
      patternSelectorSetting = (Setting) documentState.getEnumSetting("Pattern", "Generate", PATTERN_OPTIONS,
            "Random");

      // Pattern destination dropdown
      String[] NOTEDESTINATION_OPTIONS = { "Kick", "Snare", "Rimshot", "Hi-Hat Closed", "Hi-Hat Open", "Cymbal",
            "Tom 1", "Tom 2",
            "Tom 3", "Tom 4", "Percussion 1", "Percussion 2", "Percussion 3", "Percussion 4" };
      noteDestinationSetting = (Setting) documentState.getEnumSetting("Note Destination", "Clip",
            NOTEDESTINATION_OPTIONS,
            NOTEDESTINATION_OPTIONS[0]);

      ((EnumValue) noteDestinationSetting).addValueObserver(newValue -> {
         getHost().showPopupNotification(
               newValue + ": " + Utils.getNoteNameAsString(getCurrentNoteDestinationAsInt()));
      });

      // Pattern step size
      final String[] STEPSIZE_OPTIONS = new String[] { "1/2", "1/4", "1/8", "1/8", "1/16", "1/32", "1/32", "1/64",
            "1/128",
            "1/2 - 3t", "1/4 - 3t", "1/8 - 3t", "1/8 - 3t", "1/16 - 3t", "1/32 - 3t", "1/32 - 3t", "1/64 - 3t",
            "1/128 - 3t" };
      stepSizSetting = (Setting) documentState.getEnumSetting("Step Size", "Clip", STEPSIZE_OPTIONS, "1/16");

      // Pattern note length
      noteLengthSetting = (Setting) documentState.getEnumSetting("Note Length", "Clip", STEPSIZE_OPTIONS, "1/16");

      noteChannelSetting = (Setting) documentState.getNumberSetting("Note Channel", "Clip", 1, 16, 1, "", 1);

      initAllNoteFieldsSettings();

      // Launcher/Arranger toggle
      final String[] TOGGLE_LAUNCHER_ARRANGER_OPTIONS = new String[] { "Launcher", "Arranger", };
      toggleLauncherArrangerSetting = (Setting) documentState.getEnumSetting("Destination Launcher/Arranger", "Z",
            TOGGLE_LAUNCHER_ARRANGER_OPTIONS,
            TOGGLE_LAUNCHER_ARRANGER_OPTIONS[0]);

      // Empty button
      spacerSetting = (Setting) documentState.getStringSetting(" ", "Z", 0, "--------------------------------------");
      spacerSetting.disable();

      // Clear current clip
      documentState.getSignalSetting("Clear current clip", "Z", "Clear current clip").addSignalObserver(() -> {
         getLauncherArrangerAsClip().clearSteps();
      });

      // Show a notification to confirm initialization
      host.showPopupNotification("BeatBuddy Initialized");

   }


   /**
    * Initialize all note fields in the UI, including the toggle to show/hide and the individual note fields.
    */
   private void initAllNoteFieldsSettings() {
      // Note destination options
      // Toggle note field
      final String[] TOGGLE_NOTE_OPTIONS = new String[] { "Show", "Hide" };
      toggleNoteFields = (Setting) documentState.getEnumSetting("Show Options", "Notes", TOGGLE_NOTE_OPTIONS,
            TOGGLE_NOTE_OPTIONS[0]);
      // Fields for note destination

      noteKickSetting = (Setting) documentState.getNumberSetting("Kick", "Notes", 0, 128, 1, noteUnitSetting, 36);
      noteSnareSetting = (Setting) documentState.getNumberSetting("Snare", "Notes", 0, 128, 1, noteUnitSetting, 37);
      noteRimshotSetting = (Setting) documentState.getNumberSetting("Rimshot", "Notes", 0, 128, 1, noteUnitSetting, 38);
      noteClosedHiHatSetting = (Setting) documentState.getNumberSetting("Hi-Hat Open", "Notes", 0, 128, 1,
            noteUnitSetting, 42);
      noteOpenHiHatSetting = (Setting) documentState.getNumberSetting("Hi-Hat Closed", "Notes", 0, 128, 1,
            noteUnitSetting, 46);
      noteCymbalSetting = (Setting) documentState.getNumberSetting("Cymbal", "Notes", 0, 128, 1, noteUnitSetting, 59);
      noteTom1Setting = (Setting) documentState.getNumberSetting("Tom 1", "Notes", 0, 128, 1, noteUnitSetting, 47);
      noteTom2Setting = (Setting) documentState.getNumberSetting("Tom 2", "Notes", 0, 128, 1, noteUnitSetting, 53);
      noteTom3Setting = (Setting) documentState.getNumberSetting("Tom 3", "Notes", 0, 128, 1, noteUnitSetting, 54);
      noteTom4Setting = (Setting) documentState.getNumberSetting("Tom 4", "Notes", 0, 128, 1, noteUnitSetting, 55);
      notePerc1Setting = (Setting) documentState.getNumberSetting("Percussion 1", "Notes", 0, 128, 1, noteUnitSetting,
            38);
      notePerc2Setting = (Setting) documentState.getNumberSetting("Percussion 2", "Notes", 0, 128, 1, noteUnitSetting,
            40);
      notePerc3Setting = (Setting) documentState.getNumberSetting("Percussion 3", "Notes", 0, 128, 1, noteUnitSetting,
            41);
      notePerc4Setting = (Setting) documentState.getNumberSetting("Percussion 4", "Notes", 0, 128, 1, noteUnitSetting,
            56);

      // Show/Hide note fields
      Setting[] allNoteFields = { noteKickSetting, noteSnareSetting, noteRimshotSetting, noteClosedHiHatSetting,
            noteOpenHiHatSetting, noteCymbalSetting,
            noteTom1Setting, noteTom2Setting,
            noteTom3Setting, noteTom4Setting, notePerc1Setting, notePerc2Setting, notePerc3Setting, notePerc4Setting };

      for (Setting noteSetting : allNoteFields) {
         ((SettableRangedValue) noteSetting).addValueObserver(newValue -> {
            getHost().showPopupNotification(
                  noteSetting.getLabel() + ": " + Utils.getNoteNameAsString(getNoteValue(noteSetting)));
         });
      }

      ((EnumValue) toggleNoteFields).addValueObserver((String newValue) -> {
         for (Setting noteField : allNoteFields) {

            if (newValue.equals("Show")) {
               noteField.show();
            } else {
               noteField.hide();
            }
         }
      });
   }

   /**
    * Returns the MIDI note value of the given note setting.
    * 
    * @param noteSetting The note setting to get the value of.
    * @return The MIDI note value of the given note setting.
    */
   private int getNoteValue(Setting noteSetting) {
      return (int) Math.round(((SettableRangedValue) noteSetting).getRaw());
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
      String currentNoteString = ((EnumValue) noteDestinationSetting).get();
      int currentValueInt = 0;

      switch (currentNoteString) {
         case "Kick":
            currentValueInt = getNoteValue(noteKickSetting);
            break;
         case "Snare":
            currentValueInt = getNoteValue(noteSnareSetting);
            break;
         case "Hi-Hat Closed":
            currentValueInt = getNoteValue(noteClosedHiHatSetting);
            break;
         case "Hi-Hat Open":
            currentValueInt = getNoteValue(noteOpenHiHatSetting);
            break;
         case "Cymbal":
            currentValueInt = getNoteValue(noteCymbalSetting);
            break;
         case "Tom 1":
            currentValueInt = getNoteValue(noteTom1Setting);
            break;
         case "Tom 2":
            currentValueInt = getNoteValue(noteTom2Setting);
            break;
         case "Tom 3":
            currentValueInt = getNoteValue(noteTom3Setting);
            break;
         case "Tom 4":
            currentValueInt = getNoteValue(noteTom4Setting);
            break;
         case "Percussion 1":
            currentValueInt = getNoteValue(notePerc1Setting);
            break;
         case "Percussion 2":
            currentValueInt = getNoteValue(notePerc2Setting);
            break;
         case "Percussion 3":
            currentValueInt = getNoteValue(notePerc3Setting);
            break;
         case "Percussion 4":
            currentValueInt = getNoteValue(notePerc4Setting);
            break;
         default:
            getHost().showPopupNotification("Unknown note type: " + currentNoteString);
            break;
      }

      getHost().showPopupNotification("Selected Note: " + currentNoteString);
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
    * Converts a note length selected by the user to a duration value.
    *
    * @param selectedNoteLength The note length selected by the user. Valid values
    *                           are:
    *                           1/2, 1/4, 1/8, 1/16, 1/32, 1/64, 1/128, 1/2 - 3t,
    *                           1/4 - 3t, 1/8 - 3t, 1/16 - 3t, 1/32 - 3t, 1/64 -
    *                           3t, 1/128 - 3t.
    * @return A double representing the duration value of the selected note length.
    */
   private double getNoteLengthAsDouble(String selectedNoteLength) {
      double duration = 1.0;
      switch (selectedNoteLength) {
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
         case "1/2 - 3t":
            duration = 2.0 * (2.0 / 3.0); // 1.3333...
            break;
         case "1/4 - 3t":
            duration = 1.0 * (2.0 / 3.0); // 0.6667...
            break;
         case "1/8 - 3t":
            duration = 0.5 * (2.0 / 3.0); // 0.3333...
            break;
         case "1/16 - 3t":
            duration = 0.25 * (2.0 / 3.0); // 0.1667...
            break;
         case "1/32 - 3t":
            duration = 0.125 * (2.0 / 3.0); // 0.08333...
            break;
         case "1/64 - 3t":
            duration = 0.0625 * (2.0 / 3.0); // 0.04167...
            break;
         case "1/128 - 3t":
            duration = 0.03125 * (2.0 / 3.0); // 0.02083...
            break;
         default:
            // Handle unexpected stepSize values.
            duration = 1.0;
            break;
      }

      return duration;
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
    * Generates a drum pattern with the selected step size and note length.
    * 
    * If the selected pattern is "Random", a random pattern is generated.
    * 
    * The pattern is then written to the currently selected clip (Launcher or
    * Arranger) at the currently selected note destination and channel.
    */
   private void generateDrumPattern() {
      String selectedNoteLength = ((EnumValue) noteLengthSetting).get(); // Get the current selected value of noteLength
      double durationValue = getNoteLengthAsDouble(selectedNoteLength);

      String selectedStepSize = ((EnumValue) stepSizSetting).get(); // Get the current selected value of stepSize
      double stepSize = getNoteLengthAsDouble(selectedStepSize);
      getLauncherArrangerAsClip().setStepSize(stepSize);

      int channel = getCurrentChannelAsDouble();
      int y = getCurrentNoteDestinationAsInt();
      // getLauncherArrangerAsClip().scrollToKey(y);

      getLauncherArrangerAsClip().clearStepsAtY(channel, y);

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
      // getLauncherArrangerAsClip().setStepSize(3);
      for (int i = 0; i < currentPattern.length; i++) {
         if (currentPattern[i] > 0) {
            getLauncherArrangerAsClip().setStep(channel, i, y, currentPattern[i], durationValue);
         }
      }

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
