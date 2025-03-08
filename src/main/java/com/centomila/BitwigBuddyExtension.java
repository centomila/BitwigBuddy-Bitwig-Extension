package com.centomila;

import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.CueMarker;
import com.bitwig.extension.controller.api.CueMarkerBank;
import com.bitwig.extension.controller.ControllerExtension;
import com.bitwig.extension.controller.api.Application;
import com.bitwig.extension.controller.api.Arranger;
import com.bitwig.extension.controller.api.Clip;
import com.bitwig.extension.controller.api.DocumentState;
import com.bitwig.extension.controller.api.Project;
import com.bitwig.extension.controller.api.Transport;
import com.centomila.utils.PopupUtils;
import com.centomila.utils.SettingsHelper;
import com.centomila.utils.ExtensionPath;

/**
 * BitwigBuddy Extension for Bitwig Studio.
 * This extension provides functionality for generating and manipulating drum
 * patterns
 * in both launcher and arranger clips. It features pattern generation, step
 * movement,
 * and various clip manipulation tools.
 */
public class BitwigBuddyExtension extends ControllerExtension {
   public Application application;
   public Clip cursorClip;
   public Clip arrangerClip;
   public Arranger arranger;
   public Transport transport;
   public Project project;
   public CueMarkerBank cueMarkerBank;
   public CueMarker cueMarker;

   DocumentState documentState;

   // Step movement settings
   private MoveStepsHandler moveStepsHandler;

   

   GlobalPreferences preferences;

   protected BitwigBuddyExtension(final BitwigBuddyExtensionDefinition definition, final ControllerHost host) {
      super(definition, host);
   }

   @Override
   public void init() {
      final ControllerHost host = getHost();
      ExtensionPath.init(host);
      PopupUtils.init(host);

      preferences = new GlobalPreferences(host, this);

      // Initialize API objects
      application = host.createApplication();
      cursorClip = host.createLauncherCursorClip((16 * 8), 128);
      arrangerClip = host.createArrangerCursorClip((16 * 8), 128);
      documentState = host.getDocumentState();
      transport = host.createTransport();
      arranger = host.createArranger();
      project = host.getProject();
      cueMarkerBank = arranger.createCueMarkerBank(128);
      
      cueMarkerBank.subscribe();
      for (int i = 0; i < 128; i++) {
         cueMarkerBank.getItemAt(i).name().markInterested();
         cueMarkerBank.getItemAt(i).getColor().markInterested();
         cueMarkerBank.getItemAt(i).exists().markInterested();
         cueMarkerBank.getItemAt(i).position().markInterested();

      }
      cueMarkerBank.scrollPosition().markInterested();
      cueMarkerBank.itemCount().markInterested();

      this.application.panelLayout().markInterested();

      cursorClip.getLoopLength().markInterested();
      cursorClip.getLoopStart().markInterested();
      cursorClip.getPlayStart().markInterested();
      cursorClip.getPlayStop().markInterested();
      cursorClip.clipLauncherSlot().isPlaying().markInterested();
      arrangerClip.getLoopLength().markInterested();
      arrangerClip.getLoopStart().markInterested();
      arrangerClip.getPlayStart().markInterested();
      arrangerClip.getPlayStop().markInterested();
      arrangerClip.clipLauncherSlot().isPlaying().markInterested();

      
      
      

      SettingsHelper.init(this);
      
      // Initialize launcher/arranger toggle
      
      ModeSelectSettings.init(this);

      moveStepsHandler = new MoveStepsHandler(this);
      moveStepsHandler.init(documentState);
      
      EditClipSettings.init(this);
      
      PatternSettings.init(this);
      ProgramPattern.init(this);
      NoteDestinationSettings.init(this);
      StepSizeSettings.init(this);
      PostActionSettings.init(this);
      ClipUtils.init(this);
      MacroActionSettings.init(this);


      
      
      ModeSelectSettings.gotoGenerateMode();


      // Show a notification to confirm initialization
      PopupUtils.showPopup("BitwigBuddy Initialized! Have fun!");
   }




   /**
    * Generates a drum pattern based on current settings.
    * Uses the selected pattern template, note destination, and timing settings
    * to create a new pattern in the active clip.
    */
   public void generateDrumPattern() {
      Clip clip = getLauncherOrArrangerAsClip();
      DrumPatternGenerator.generatePattern(
            this, clip);
   }

   /**
    * Returns the currently active clip based on the launcher/arranger toggle
    * setting.
    * 
    * @return The active Clip object (either launcher or arranger clip)
    */
   public Clip getLauncherOrArrangerAsClip() {
      return ClipUtils.getLauncherOrArrangerAsClip(ModeSelectSettings.toggleLauncherArrangerSetting, arrangerClip, cursorClip);
   }

   @Override
   public void exit() {
      // Cleanup on exit
      PopupUtils.showPopup("BitwigBuddy Exited");
   }

   @Override
   public void flush() {
      // Update logic if necessary
   }

   // Getters and Setters

   public Application getApplication() {
      return application;
   }

   public DocumentState getDocumentState() {
      return documentState;
   }

   public MoveStepsHandler getMoveStepsHandler() {
      return moveStepsHandler;
   }

   public void setMoveStepsHandler(MoveStepsHandler moveStepsHandler) {
      this.moveStepsHandler = moveStepsHandler;
   }

   public void restart() {
      getHost().restart();
   }

}
