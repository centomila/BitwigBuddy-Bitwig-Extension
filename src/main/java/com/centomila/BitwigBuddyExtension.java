package com.centomila;

import com.bitwig.extension.controller.api.ControllerHost;

import static com.centomila.utils.SettingsHelper.*;

import com.bitwig.extension.controller.ControllerExtension;
import com.bitwig.extension.controller.api.Application;
import com.bitwig.extension.controller.api.Clip;
import com.bitwig.extension.controller.api.DocumentState;
import com.bitwig.extension.controller.api.EnumValue;
import com.bitwig.extension.controller.api.Setting;

import com.centomila.utils.PopupUtils;
import com.centomila.utils.SettingsHelper;

/**
 * BitwigBuddy Extension for Bitwig Studio.
 * This extension provides functionality for generating and manipulating drum
 * patterns
 * in both launcher and arranger clips. It features pattern generation, step
 * movement,
 * and various clip manipulation tools.
 */
public class BitwigBuddyExtension extends ControllerExtension {
   Application application;
   Clip cursorClip;
   Clip arrangerClip;

   DocumentState documentState;
   // Pattern settings
   Setting patternTypeSetting; // Pattern Type "Preset", "Random", "Custom"
   Setting patternSelectorSetting; // List of default patterns
   Setting customPresetSetting; // List of custom patterns
   Setting presetPatternStringSetting; // Custom pattern string
   Setting reversePatternSetting;

   // Random Settings
   Setting randomMinVelocityVariationSetting;
   Setting randomMaxVelocityVariationSetting;
   Setting randomDensitySetting;
   Setting randomVelocitySettingShape;
   Setting randomStepQtySetting;

   // Step Size / Note Length settings
   Setting noteLengthSetting; // How long each note should be
   Setting stepSizSetting;
   Setting stepSizSubdivisionSetting; // Subdivisions Straight | Dotted | Triplet | Quintuplet | Septuplet
   Setting learnNoteSetting; // On or Off

   // Note Destination settings
   Setting noteDestinationSetting; // Note Destination "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#",
                                   // "B"
   Setting noteOctaveSetting; // Note Octave -2 to 8
   Setting noteChannelSetting; // Note Channel 1 to 16
   NoteDestinationSettings noteDestSettings; // Class to handle note destination settings

   // Post actions settings
   Setting autoResizeLoopLengthSetting;
   Setting zoomToFitAfterGenerateSetting;
   Setting postActionsSetting;

   Setting duplicateClipSetting;
   Setting openInDetailEditorSetting;

   // Step movement settings
   private MoveStepsHandler moveStepsHandler;

   Setting toggleLauncherArrangerSetting;

   GlobalPreferences preferences;

   protected BitwigBuddyExtension(final BitwigBuddyExtensionDefinition definition, final ControllerHost host) {
      super(definition, host);
   }

   @Override
   public void init() {
      final ControllerHost host = getHost();
      preferences = new GlobalPreferences(host);

      // Initialize API objects
      application = host.createApplication();
      cursorClip = host.createLauncherCursorClip((16 * 8), 128);
      arrangerClip = host.createArrangerCursorClip((16 * 8), 128);
      documentState = host.getDocumentState();

      cursorClip.getLoopLength().markInterested();
      cursorClip.getLoopStart().markInterested();
      cursorClip.getPlayStart().markInterested();
      cursorClip.getPlayStop().markInterested();
      arrangerClip.getLoopLength().markInterested();
      arrangerClip.getLoopStart().markInterested();
      arrangerClip.getPlayStart().markInterested();
      arrangerClip.getPlayStop().markInterested();

      moveStepsHandler = new MoveStepsHandler(this);
      moveStepsHandler.init(documentState);
      
      SettingsHelper.init(this);
      PopupUtils.init(host);

      PatternSettings.init(this);
      RandomPattern.init(this);
      NoteDestinationSettings.init(this);
      StepSizeSettings.init(this);
      PostActionSettings.init(this);
      ClipUtils.init(this);

      // Initialize launcher/arranger toggle
      initToggleLauncherArrangerSetting();

      // Show a notification to confirm initialization
      PopupUtils.showPopup("BitwigBuddy Initialized");

   }

   /**
    * Initializes the clip destination toggle.
    * Allows switching between launcher and arranger clip modes,
    * determining where patterns will be generated.
    */
   private void initToggleLauncherArrangerSetting() {
      // Launcher/Arranger toggle
      final String[] TOGGLE_LAUNCHER_ARRANGER_OPTIONS = new String[] { "Launcher", "Arranger", };
      toggleLauncherArrangerSetting = (Setting) documentState.getEnumSetting("Destination Launcher/Arranger", "Z",
            TOGGLE_LAUNCHER_ARRANGER_OPTIONS,
            TOGGLE_LAUNCHER_ARRANGER_OPTIONS[0]);

      ((EnumValue) toggleLauncherArrangerSetting).addValueObserver(newValue -> {
         PopupUtils.showPopup("Destination: " + newValue);
         if (newValue.equals("Arranger")) {
            disableSetting(duplicateClipSetting);
         } else {
            enableSetting(duplicateClipSetting);
         }
      });
   }

   /**
    * Generates a drum pattern based on current settings.
    * Uses the selected pattern template, note destination, and timing settings
    * to create a new pattern in the active clip.
    */
   public void generateDrumPattern() {
      Clip clip = getLauncherOrArrangerAsClip();
      DrumPatternGenerator.generatePattern(
            this, clip, noteLengthSetting, stepSizSubdivisionSetting,
            stepSizSetting, noteDestSettings, patternSelectorSetting, patternTypeSetting, presetPatternStringSetting,
            reversePatternSetting, autoResizeLoopLengthSetting, zoomToFitAfterGenerateSetting);
   }

   /**
    * Returns the currently active clip based on the launcher/arranger toggle
    * setting.
    * 
    * @return The active Clip object (either launcher or arranger clip)
    */
   public Clip getLauncherOrArrangerAsClip() {
      return ClipUtils.getLauncherOrArrangerAsClip(toggleLauncherArrangerSetting, arrangerClip, cursorClip);
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

   public void setPatternSelectorSetting(Setting patternSelectorSetting) {
      this.patternSelectorSetting = patternSelectorSetting;
   }

   public void setCustomPresetSetting(Setting customPresetSetting) {
      this.customPresetSetting = customPresetSetting;
   }

   public void setNoteLengthSetting(Setting noteLengthSetting) {
      this.noteLengthSetting = noteLengthSetting;
   }

   public void setStepSizSetting(Setting stepSizSetting) {
      this.stepSizSetting = stepSizSetting;
   }

   public void setStepSizSubdivisionSetting(Setting stepSizSubdivisionSetting) {
      this.stepSizSubdivisionSetting = stepSizSubdivisionSetting;
   }

   public void setNoteDestinationSetting(Setting noteDestinationSetting) {
      this.noteDestinationSetting = noteDestinationSetting;
   }

   public void setNoteOctaveSetting(Setting noteOctaveSetting) {
      this.noteOctaveSetting = noteOctaveSetting;
   }

   public void setNoteChannelSetting(Setting noteChannelSetting) {
      this.noteChannelSetting = noteChannelSetting;
   }

   public void setToggleLauncherArrangerSetting(Setting toggleLauncherArrangerSetting) {
      this.toggleLauncherArrangerSetting = toggleLauncherArrangerSetting;
   }

   public void setAutoResizeLoopLengthSetting(Setting autoResizeLoopLengthSetting) {
      this.autoResizeLoopLengthSetting = autoResizeLoopLengthSetting;
   }

   public void setReversePatternSetting(Setting reversePatternSetting) {
      this.reversePatternSetting = reversePatternSetting;
   }

   public MoveStepsHandler getMoveStepsHandler() {
      return moveStepsHandler;
   }

   public void setMoveStepsHandler(MoveStepsHandler moveStepsHandler) {
      this.moveStepsHandler = moveStepsHandler;
   }

   public void setNoteDestSettings(NoteDestinationSettings noteDestSettings) {
      this.noteDestSettings = noteDestSettings;
   }

   public void setPreferences(GlobalPreferences preferences) {
      this.preferences = preferences;
   }

   public Setting getZoomToFitAfterGenerateSetting() {
      return zoomToFitAfterGenerateSetting;
   }

   public void setZoomToFitAfterGenerateSetting(Setting zoomToFitAfterGenerateSetting) {
      this.zoomToFitAfterGenerateSetting = zoomToFitAfterGenerateSetting;
   }

   public void setDuplicateClipSetting(Setting openInDetailEditorSetting) {
      this.duplicateClipSetting = openInDetailEditorSetting;
   }

   public Setting getPostActionsSetting() {
      return postActionsSetting;
   }

   public void setPostActionsSetting(Setting postActionsSetting) {
      this.postActionsSetting = postActionsSetting;
   }

   public Setting getPresetPatternStringSetting() {
      return presetPatternStringSetting;
   }

   public void setPresetPatternStringSetting(Setting customPresetPatternSetting) {
      this.presetPatternStringSetting = customPresetPatternSetting;
   }

}
