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

public class BeatBuddyExtension extends ControllerExtension {
   private Application application;
   private Clip cursorClip;
   private CursorTrack cursorTrack;
   private DocumentState documentState;
   private Setting genreSelector;
   private Setting clipLength;
   private Setting quantization;
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
      cursorClip = host.createLauncherCursorClip(8, 128);
      cursorTrack = host.createCursorTrack(0, 0);
      documentState = host.getDocumentState();

      final String[] QUANTIZATION_OPTIONS = new String[] { "1/4", "1/8", "1/16", "1/32", "1/64", "1/128" };
      // Define pattern settings
      String[] genres = new String[] { "Techno", "House", "Deep House", "Dubstep" };
      
      genreSelector = (Setting) documentState.getEnumSetting("Genre", "Genre", genres, "Techno");
      
      clipLength = (Setting) documentState.getNumberSetting("Clip Length (Bars)", "Clip", 1, 16, 4, "Bar(s)", 1);
      quantization = (Setting) documentState.getEnumSetting("Quantization", "Clip", QUANTIZATION_OPTIONS, "1/16");
      
      velocityVariation = (Setting) documentState.getBooleanSetting("Velocity Variation", "Clip", true);
      humanization = (Setting) documentState.getBooleanSetting("Humanization", "Clip", true);

      documentState.getSignalSetting("Generate!", "Generate", "Generate!").addSignalObserver(() -> {
         generateDrumPattern();
      });

   }

   private void generateDrumPattern() {
      String selectedGenre = ((EnumValue) genreSelector).get(); // Get the current selected value of genreSelector
      String selectedQuantization = ((EnumValue) quantization).get(); // Get the current selected value of quantization


      String settingsString = "Genre: " + selectedGenre;
      settingsString += " - Quantization: " + selectedQuantization;

      getHost().showPopupNotification(settingsString);

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
