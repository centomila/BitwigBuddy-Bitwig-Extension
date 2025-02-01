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
import com.bitwig.extension.controller.api.IntegerValue;
import com.bitwig.extension.controller.api.SettableRangedValue;
import com.bitwig.extension.controller.api.Channel;
import com.bitwig.extension.controller.api.Setting;

public class BeatBuddyExtension extends ControllerExtension implements DrumsNotes {
   private Application application;
   private Clip cursorClip;
   private Clip clip;
   private CursorTrack cursorTrack;
   private DocumentState documentState;
   private Setting patternSelector;
   private Setting clipLength;
   private Setting noteDuration;
   private Setting velocityVariation;
   private Setting humanization;
   private Setting noteDestination;
   private Setting noteChannel;

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

      // Note Step Destination
      String[] NOTEDESTINATION_OPTIONS = Arrays.stream(DrumsNotes.gmDrums)
            .map(drumSoundArray -> Arrays.stream(drumSoundArray)
                  .map(Object::toString)
                  .reduce("", (a, b) -> a + " " + b).trim())
            .toArray(String[]::new);

      noteDestination = (Setting) documentState.getEnumSetting("Note Destination", "Clip", NOTEDESTINATION_OPTIONS,
            NOTEDESTINATION_OPTIONS[1]);

      // Define clip settings
      // clipLength = (Setting) documentState.getNumberSetting("Clip Length (Bars)
      // (TBI)", "Clip", 1, 16, 4, "Bar(s)", 1);

      final String[] NOTEDURATION_OPTIONS = new String[] { "1/4", "1/8", "1/16", "1/32", "1/64", "1/128" };
      noteDuration = (Setting) documentState.getEnumSetting("Step Duration", "Clip", NOTEDURATION_OPTIONS, "1/16");

      noteChannel = (Setting) documentState.getNumberSetting("Note Channel", "Clip", 1, 16, 1, "", 1);

      velocityVariation = (Setting) documentState.getBooleanSetting("Velocity Variation (TBI)", "Clip", true);

      // Generate button
      documentState.getSignalSetting("Generate!", "Generate", "Generate!").addSignalObserver(() -> {
         generateDrumPattern();
      });

   }

   private int getCurrentNoteDestination() {
      String[] noteDestinationStrings = ((EnumValue) noteDestination).get().split(" ");
      int currentNoteDestination = 0;
      if (noteDestinationStrings.length > 0 && noteDestinationStrings[0] != null) {
         currentNoteDestination = Integer.parseInt(noteDestinationStrings[0]);
      }
      return currentNoteDestination;
   }

   private int getCurrentChannel() {
      // The raw fraction between 0.0 and 1.0
      double fraction = ((SettableRangedValue) noteChannel).get();

      // Convert fraction to your desired 1..16 range.
      // Range span is (16 - 1) = 15
      // So fraction=0 => 1, fraction=1 => 16, fraction=0.333 => ~6, etc.
      int channel = (int) Math.round(1 + (fraction * 15));

      getHost().println("Fraction = " + fraction + ", Channel = " + channel);
      return channel-1;
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
      
      getHost().showPopupNotification( "Channel: " + channel + " Note: " + y + " Duration: " + durationValue);

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
