package com.centomila;

import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.Transport;

import java.util.Arrays;

import com.bitwig.extension.controller.ControllerExtension;
import com.bitwig.extension.controller.api.Application;
import com.bitwig.extension.controller.api.BooleanValue;
import com.bitwig.extension.controller.api.Clip;
import com.bitwig.extension.controller.api.CursorTrack;
import com.bitwig.extension.controller.api.DocumentState;
import com.bitwig.extension.controller.api.DoubleValue;
import com.bitwig.extension.controller.api.EnumValue;
import com.bitwig.extension.controller.api.RangedValue;
import com.bitwig.extension.controller.api.SettableRangedValue;
import com.bitwig.extension.controller.api.SettableIntegerValue;
import com.bitwig.extension.controller.api.SettableDoubleValue;

import com.bitwig.extension.controller.api.Value;
import com.bitwig.extension.controller.api.IntegerValue;
import com.bitwig.extension.controller.api.Channel;
import com.bitwig.extension.controller.api.Setting;

public class BeatBuddyExtension extends ControllerExtension implements DrumsNotes {
   private Application application;
   private Clip cursorClip;
   private Clip clip;
   private CursorTrack cursorTrack;
   private DocumentState documentState;
   private Setting patternSelector;
   private Setting noteDuration;
   private Setting noteDestination;
   private Setting noteChannel;
   // settings for separate notes
   private Setting noteKick;
   private Setting noteSnare;
   private Setting noteRim;
   private Setting noteClosedHiHat;
   private Setting noteOpenHiHat;
   private Setting noteCymbal;
   private Setting noteTom1;
   private Setting noteTom2;
   private Setting noteTom3;
   private Setting noteTom4;
   private Setting notePerc1;
   private Setting notePerc2;
   private Setting notePerc3;
   private Setting notePerc4;

   protected BeatBuddyExtension(final BeatBuddyExtensionDefinition definition, final ControllerHost host) {
      super(definition, host);
   }

   @Override
   public void init() {
      final ControllerHost host = getHost();

      // Show a notification to confirm initialization
      host.showPopupNotification("BeatBuddy Initialized");

      // Initialize API objects
      application = host.createApplication();
      cursorClip = host.createLauncherCursorClip((16 * 8), 128);
      cursorTrack = host.createCursorTrack((16 * 8), 128);
      documentState = host.getDocumentState();

      // Define pattern settings
      final String[] PATTERN_OPTIONS = Arrays.stream(DrumPatterns.patterns)
            .map(pattern -> pattern[0].toString())
            .toArray(String[]::new);

      patternSelector = (Setting) documentState.getEnumSetting("Pattern", "Pattern", PATTERN_OPTIONS,
            PATTERN_OPTIONS[0]);

      noteKick = (Setting) documentState.getNumberSetting("Kick", "Notes", 0, 128, 1, "Note Number", 36);
      noteSnare = (Setting) documentState.getNumberSetting("Snare", "Notes", 0, 128, 1, "Note Number", 37);
      noteClosedHiHat = (Setting) documentState.getNumberSetting("Hi-Hat Open", "Notes", 0, 128, 1, "Note Number", 42);
      noteOpenHiHat = (Setting) documentState.getNumberSetting("Hi-Hat Closed", "Notes", 0, 128, 1, "Note Number", 46);
      noteCymbal = (Setting) documentState.getNumberSetting("Cymbal", "Notes", 0, 128, 1, "Note Number", 59);
      noteTom1 = (Setting) documentState.getNumberSetting("Tom 1", "Notes", 0, 128, 1, "Note Number", 47);
      noteTom2 = (Setting) documentState.getNumberSetting("Tom 2", "Notes", 0, 128, 1, "Note Number", 53);
      noteTom3 = (Setting) documentState.getNumberSetting("Tom 3", "Notes", 0, 128, 1, "Note Number", 54);
      noteTom4 = (Setting) documentState.getNumberSetting("Tom 4", "Notes", 0, 128, 1, "Note Number", 55);
      notePerc1 = (Setting) documentState.getNumberSetting("Percussion 1", "Notes", 0, 128, 1, "Note Number", 38);
      notePerc2 = (Setting) documentState.getNumberSetting("Percussion 2", "Notes", 0, 128, 1, "Note Number", 40);
      notePerc3 = (Setting) documentState.getNumberSetting("Percussion 3", "Notes", 0, 128, 1, "Note Number", 41);
      notePerc4 = (Setting) documentState.getNumberSetting("Percussion 4", "Notes", 0, 128, 1, "Note Number", 56);

      // Note Step Destination
      String[] NOTEDESTINATION_OPTIONS = { "Kick", "Snare", "Hi-Hat Closed", "Hi-Hat Open", "Cymbal", "Tom 1", "Tom 2",
            "Tom 3", "Tom 4", "Percussion 1", "Percussion 2", "Percussion 3", "Percussion 4" };

      noteDestination = (Setting) documentState.getEnumSetting("Note Destination", "Clip", NOTEDESTINATION_OPTIONS,
            NOTEDESTINATION_OPTIONS[1]);

      final String[] NOTEDURATION_OPTIONS = new String[] { "1/4", "1/8", "1/16", "1/32", "1/64", "1/128" };
      noteDuration = (Setting) documentState.getEnumSetting("Step Duration", "Clip", NOTEDURATION_OPTIONS, "1/16");

      noteChannel = (Setting) documentState.getNumberSetting("Note Channel", "Clip", 1, 16, 1, "", 1);

      // Generate button
      documentState.getSignalSetting("Generate!", "Generate", "Generate!").addSignalObserver(() -> {
         // getCurrentNoteDestination();
         generateDrumPattern();
      });

   }

   private int getCurrentNoteDestination() {
      String currentNoteString = ((EnumValue) noteDestination).get();
      int currentValueInt = 0;
      switch (currentNoteString) {
         case "Kick":
            currentValueInt = (int) Math.round(((SettableRangedValue) noteKick).getRaw());
            break;
         case "Snare":
            currentValueInt = (int) Math.round(((SettableRangedValue) noteSnare).getRaw());
            break;
         case "Hi-Hat Closed":
            currentValueInt = (int) Math.round(((SettableRangedValue) noteClosedHiHat).getRaw());
            break;
         case "Hi-Hat Open":
            currentValueInt = (int) Math.round(((SettableRangedValue) noteOpenHiHat).getRaw());
            break;
         case "Cymbal":
            currentValueInt = (int) Math.round(((SettableRangedValue) noteCymbal).getRaw());
            break;
         case "Tom 1":
            currentValueInt = (int) Math.round(((SettableRangedValue) noteTom1).getRaw());
            break;
         case "Tom 2":
            currentValueInt = (int) Math.round(((SettableRangedValue) noteTom2).getRaw());
            break;
         case "Tom 3":
            currentValueInt = (int) Math.round(((SettableRangedValue) noteTom3).getRaw());
            break;
         case "Tom 4":
            currentValueInt = (int) Math.round(((SettableRangedValue) noteTom4).getRaw());
            break;
         case "Percussion 1":
            currentValueInt = (int) Math.round(((SettableRangedValue) notePerc1).getRaw());
            break;
         case "Percussion 2":
            currentValueInt = (int) Math.round(((SettableRangedValue) notePerc2).getRaw());
            break;
         case "Percussion 3":
            currentValueInt = (int) Math.round(((SettableRangedValue) notePerc3).getRaw());
            break;
         case "Percussion 4":
            currentValueInt = (int) Math.round(((SettableRangedValue) notePerc4).getRaw());
            break;
         default:
            break;
      }

      getHost().showPopupNotification(currentValueInt + " selected");
      return currentValueInt;
   }

   private int getCurrentChannel() {
      int channel = (int) Math.round(((SettableRangedValue) noteChannel).getRaw());
      return channel;
   }

   private double selectedDurationValue(String selectedNoteDuration) {
      // Use the value from noteDuration to determine the duration of the note.
      // Options are "1/4", "1/8", "1/16", "1/32", "1/64", "1/128". 1/4 = 1.0. 1/8 =
      // 0.5, etc.
      double durationValue = 1.0;
      switch (selectedNoteDuration) {
         case "1/4":
            durationValue = 1.0;
            break;
         case "1/8":
            durationValue = 0.5;
            break;
         case "1/16":
            durationValue = 0.25;
            break;
         case "1/32":
            durationValue = 0.125;
            break;
         case "1/64":
            durationValue = 0.0625;
            break;
         case "1/128":
            durationValue = 0.03125;
            break;
      }
      return durationValue;
   }

   private void generateDrumPattern() {
      String selectedNoteDuration = ((EnumValue) noteDuration).get(); // Get the current selected value of noteDuration

      // popout notification

      int channel = getCurrentChannel();
      int x = 0;
      int y = getCurrentNoteDestination();
      double durationValue = selectedDurationValue(selectedNoteDuration);

      getHost().showPopupNotification("Channel: " + channel + " Note: " + y + " Duration: " + durationValue);

      for (int i = 0; i < 16; i++) {
         cursorClip.clearStep(i, y);
      }

      String selectedPattern = ((EnumValue) patternSelector).get();
      int[] currentPattern = DrumPatterns.getPatternByName(selectedPattern);

      for (int i = 0; i < currentPattern.length; i++) {
         if (currentPattern[i] > 0) {
            cursorClip.setStep(channel, i, y, currentPattern[i], durationValue);
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
