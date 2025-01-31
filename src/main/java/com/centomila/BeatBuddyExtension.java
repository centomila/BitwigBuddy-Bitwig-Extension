package com.centomila;

import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.Transport;
import com.bitwig.extension.controller.ControllerExtension;
import com.bitwig.extension.controller.api.Application;
import com.bitwig.extension.controller.api.BooleanValue;
import com.bitwig.extension.controller.api.Clip;
import com.bitwig.extension.controller.api.CursorTrack;
import com.bitwig.extension.controller.api.DocumentState;
import com.bitwig.extension.controller.api.EnumValue;
import com.bitwig.extension.controller.api.Setting;

public class BeatBuddyExtension extends ControllerExtension implements GmDrums {
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

      final String[] NOTEDURATION_OPTIONS = new String[] { "1/4", "1/8", "1/16", "1/32", "1/64", "1/128" };
      // Define pattern settings
      String[] pattern = new String[] { "Four on the Floor", "Pattern 2", "Pattern 3", "Pattern 4", "Pattern 5" };

      patternSelector = (Setting) documentState.getEnumSetting("Pattern (TBI)", "Pattern", pattern,
            "Four on the Floor");

      // Note Step Destination
      String[] NOTEDESTINATION_OPTIONS = new String[GmDrums.drumSounds.length];
      for (int i = 0; i < GmDrums.drumSounds.length; i++) {
         String drumSound = "";
         for (Object value : GmDrums.drumSounds[i]) {
            drumSound += value.toString() + " ";
         }
         drumSound = drumSound.trim();
         if (drumSound != null) {
            NOTEDESTINATION_OPTIONS[i] = drumSound;
         }
      }
      noteDestination = (Setting) documentState.getEnumSetting("Note Destination", "Clip", NOTEDESTINATION_OPTIONS,
            NOTEDESTINATION_OPTIONS[1]);

      // Define clip settings
      // clipLength = (Setting) documentState.getNumberSetting("Clip Length (Bars) (TBI)", "Clip", 1, 16, 4, "Bar(s)", 1);
      noteDuration = (Setting) documentState.getEnumSetting("Step Duration", "Clip", NOTEDURATION_OPTIONS, "1/16");

      velocityVariation = (Setting) documentState.getBooleanSetting("Velocity Variation (TBI)", "Clip", true);

      documentState.getSignalSetting("Generate!", "Generate", "Generate!").addSignalObserver(() -> {
         generateDrumPattern();
      });

   }
   
   private int getCurrentNoteDestination() {
      String selectedNoteDestination = ((EnumValue) noteDestination).get(); // Get the current selected value of noteDestination
      int currentNoteDestination = 0;
      if (selectedNoteDestination != null) {
         String[] noteDestinationStrings = selectedNoteDestination.split(" ");
         if (noteDestinationStrings.length > 0 && noteDestinationStrings[0] != null) {
            try {
               currentNoteDestination = Integer.parseInt(noteDestinationStrings[0]);
            } catch (NumberFormatException e) {
               getHost().showPopupNotification("Error: Note destination is not a valid number");
            }
         }
      }

      return currentNoteDestination;
      
   }

   private void generateDrumPattern() {
      String selectedGenre = ((EnumValue) patternSelector).get(); // Get the current selected value of genreSelector
      String selectedNoteDuration = ((EnumValue) noteDuration).get(); // Get the current selected value of noteDuration

      String settingsString = "Genre: " + selectedGenre;
      settingsString += " - noteDuration: " + selectedNoteDuration;

      getHost().showPopupNotification(settingsString);

      // channel - the channel of the new note
      // x - the x position within the note grid, defining the step/time of the new note
      // y - the y position within the note grid, defining the key of the new note. Use GMDrums constants to define the note number
      // insertVelocity - the velocity of the new note. Default is 100
      // insertDuration - the duration of the new note. Default is 1.

      int channel = 0;
      int x = 0;
      int y = getCurrentNoteDestination();

      // Use the value from noteDuration to determine the duration of the note. Options are "1/4", "1/8", "1/16", "1/32", "1/64", "1/128". 1/4 = 1.0. 1/8 = 0.5, etc.
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

      for (int i = 0; i < 16; i++) {
         cursorClip.clearStep(i, y);
      }

      final int[] fourOnFour = { 127, 0, 0, 0, 100, 0, 0, 0, 127, 0, 0, 0, 100, 0, 0, 0 };

      for (int i = 0; i < fourOnFour.length; i++) {
         if (fourOnFour[i] > 0) {
            cursorClip.setStep(channel, i, y, fourOnFour[i], durationValue);
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
