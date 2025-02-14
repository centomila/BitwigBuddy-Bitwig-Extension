package com.centomila;

import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.ControllerExtension;
import com.bitwig.extension.controller.api.Application;
import com.bitwig.extension.controller.api.Clip;
import com.bitwig.extension.controller.api.DocumentState;
import com.bitwig.extension.controller.api.Setting;

import com.centomila.utils.PopupUtils;

/**
 * BeatBuddy Extension for Bitwig Studio.
 * This extension provides functionality for generating and manipulating drum
 * patterns
 * in both launcher and arranger clips. It features pattern generation, step
 * movement,
 * and various clip manipulation tools.
 */
public class BeatBuddyExtension extends ControllerExtension {
   public Application application;
   public Clip cursorClip;
   public Clip arrangerClip;

   private DocumentState documentState;
   // Pattern settings
   public Setting patternTypeSetting; // Pattern Type "Preset", "Random", "Custom"
   public Setting patternSelectorSetting; // List of default patterns
   public Setting customPresetSetting; // List of custom patterns
   public Setting presetPatternStringSetting; // Custom pattern string
   private Setting reversePatternSetting;
   
   // Step Size / Note Length settings
   public Setting noteLengthSetting; // How long each note should be
   public Setting stepSizSetting;
   public Setting stepSizSubdivisionSetting; // Subdivisions Straight | Dotted | Triplet | Quintuplet | Septuplet
   public Setting learnNoteSetting; // On or Off
   
   
   public Setting noteDestinationSetting; // Note Destination "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"
   public Setting noteOctaveSetting; // Note Octave -2 to 8
   public Setting noteChannelSetting; // Note Channel 1 to 16
   private NoteDestinationSettings noteDestSettings; // Class to handle note destination settings
   
   
   // Post actions settings
   private Setting autoResizeLoopLengthSetting;
   private Setting zoomToFitAfterGenerateSetting;
   private Setting postActionsSetting;
   
   private MoveStepsHandler moveStepsHandler;
   
   // Spacers
   private Setting spacer1;
   private Setting spacer2;
   private Setting spacer3;
   private Setting spacer4;
   
   public Setting toggleLauncherArrangerSetting;

   private GlobalPreferences preferences;

   protected BeatBuddyExtension(final BeatBuddyExtensionDefinition definition, final ControllerHost host) {
      super(definition, host);
   }

   @Override
   public void init() {
      final ControllerHost host = getHost();
      preferences = new GlobalPreferences(host);
      PopupUtils.initialize(host);
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

      PatternSettings.init(this);

      NoteDestinationSettings.init(this);

      StepSizeSettings.init(this);

      PostActionSettings.init(this);

      // Replace removed method call with external initializer
      ClipUtils.init(this);

      // Initialize launcher/arranger toggle
      initToggleLauncherArrangerSetting();

      // Initialize note input.This read the current note played and show a popup with the note name
      // initNoteInput();

      // Show a notification to confirm initialization
      PopupUtils.showPopup("BeatBuddy Initialized");

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
            stepSizSetting, noteDestSettings, patternSelectorSetting, patternTypeSetting,presetPatternStringSetting,
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
      PopupUtils.showPopup("BeatBuddy Exited");
   }

   @Override
   public void flush() {
      // Update logic if necessary
   }

   public Application getApplication() {
      return application;
   }


   public DocumentState getDocumentState() {
      return documentState;
   }

   public Setting getPatternTypeSetting() {
      return patternTypeSetting;
   }

   public void setPatternTypeSetting(Setting patternTypeSetting) {
      this.patternTypeSetting = patternTypeSetting;
   }

   public Setting getPatternSelectorSetting() {
      return patternSelectorSetting;
   }

   public void setPatternSelectorSetting(Setting patternSelectorSetting) {
      this.patternSelectorSetting = patternSelectorSetting;
   }

   public Setting getCustomPresetSetting() {
      return customPresetSetting;
   }

   public void setCustomPresetSetting(Setting customPresetSetting) {
      this.customPresetSetting = customPresetSetting;
   }

   public Setting getNoteLengthSetting() {
      return noteLengthSetting;
   }

   public void setNoteLengthSetting(Setting noteLengthSetting) {
      this.noteLengthSetting = noteLengthSetting;
   }

   public Setting getStepSizSetting() {
      return stepSizSetting;
   }

   public void setStepSizSetting(Setting stepSizSetting) {
      this.stepSizSetting = stepSizSetting;
   }

   public Setting getStepSizSubdivisionSetting() {
      return stepSizSubdivisionSetting;
   }

   public void setStepSizSubdivisionSetting(Setting stepSizSubdivisionSetting) {
      this.stepSizSubdivisionSetting = stepSizSubdivisionSetting;
   }

   public Setting getNoteDestinationSetting() {
      return noteDestinationSetting;
   }

   public void setNoteDestinationSetting(Setting noteDestinationSetting) {
      this.noteDestinationSetting = noteDestinationSetting;
   }

   public Setting getNoteOctaveSetting() {
      return noteOctaveSetting;
   }

   public void setNoteOctaveSetting(Setting noteOctaveSetting) {
      this.noteOctaveSetting = noteOctaveSetting;
   }

   public Setting getNoteChannelSetting() {
      return noteChannelSetting;
   }

   public void setNoteChannelSetting(Setting noteChannelSetting) {
      this.noteChannelSetting = noteChannelSetting;
   }

   public Setting getToggleLauncherArrangerSetting() {
      return toggleLauncherArrangerSetting;
   }

   public void setToggleLauncherArrangerSetting(Setting toggleLauncherArrangerSetting) {
      this.toggleLauncherArrangerSetting = toggleLauncherArrangerSetting;
   }

   public Setting getAutoResizeLoopLengthSetting() {
      return autoResizeLoopLengthSetting;
   }

   public void setAutoResizeLoopLengthSetting(Setting autoResizeLoopLengthSetting) {
      this.autoResizeLoopLengthSetting = autoResizeLoopLengthSetting;
   }

   public Setting getReversePatternSetting() {
      return reversePatternSetting;
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

   public NoteDestinationSettings getNoteDestSettings() {
      return noteDestSettings;
   }

   public void setNoteDestSettings(NoteDestinationSettings noteDestSettings) {
      this.noteDestSettings = noteDestSettings;
   }

   public Setting getSpacer1() {
      return spacer1;
   }

   public void setSpacer1(Setting spacer1) {
      this.spacer1 = spacer1;
   }

   public Setting getSpacer2() {
      return spacer2;
   }

   public void setSpacer2(Setting spacer2) {
      this.spacer2 = spacer2;
   }

   public Setting getSpacer3() {
      return spacer3;
   }

   public void setSpacer3(Setting spacer3) {
      this.spacer3 = spacer3;
   }

   public Setting getSpacer4() {
      return spacer4;
   }

   public void setSpacer4(Setting spacer4) {
      this.spacer4 = spacer4;
   }

   public GlobalPreferences getPreferences() {
      return preferences;
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
