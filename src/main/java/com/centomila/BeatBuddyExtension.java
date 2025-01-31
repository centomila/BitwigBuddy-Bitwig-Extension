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
   private Setting genreSelector;
   private Setting clipLength;
   private Setting noteDuration;
   private Setting velocityVariation;
   private Setting humanization;

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
      String[] genres = new String[] { "Techno", "House", "Deep House", "Dubstep" };
      
      genreSelector = (Setting) documentState.getEnumSetting("Genre", "Genre", genres, "Techno");
      
      clipLength = (Setting) documentState.getNumberSetting("Clip Length (Bars)", "Clip", 1, 16, 4, "Bar(s)", 1);
      noteDuration = (Setting) documentState.getEnumSetting("noteDuration", "Clip", NOTEDURATION_OPTIONS, "1/16");
      
      velocityVariation = (Setting) documentState.getBooleanSetting("Velocity Variation", "Clip", true);
      humanization = (Setting) documentState.getBooleanSetting("Humanization", "Clip", true);



      documentState.getSignalSetting("Generate!", "Generate", "Generate!").addSignalObserver(() -> {
         generateDrumPattern();
      });

   }

   private void generateDrumPattern() {
      String selectedGenre = ((EnumValue) genreSelector).get(); // Get the current selected value of genreSelector
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
      int y = GmDrums.BASS_DRUM;
      int insertVelocity = 100;
      double insertDuration = 0.25;


      cursorClip.setStep(channel, 0, y, insertVelocity, insertDuration);
      cursorClip.setStep(channel+1, 4, y, insertVelocity, insertDuration);
      cursorClip.setStep(channel+1, 8, y, insertVelocity, insertDuration);
      cursorClip.setStep(channel+1, 12, y, insertVelocity, insertDuration);
      

      

      // // Clear the cursor track
      // cursorClip.setStep(x, y,velocity, duration);

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
