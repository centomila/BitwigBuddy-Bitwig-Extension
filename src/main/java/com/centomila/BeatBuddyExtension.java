package com.centomila;

import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.ControllerExtension;
import com.bitwig.extension.controller.api.Application;
import com.bitwig.extension.controller.api.Clip;
import com.bitwig.extension.controller.api.DocumentState;
import com.bitwig.extension.controller.api.EnumValue;
import com.bitwig.extension.controller.api.SettableEnumValue;
import com.bitwig.extension.controller.api.Setting;
import com.bitwig.extension.controller.api.Signal;

import java.util.Arrays;

/**
 * BeatBuddy Extension for Bitwig Studio.
 * This extension provides functionality for generating and manipulating drum patterns
 * in both launcher and arranger clips. It features pattern generation, step movement,
 * and various clip manipulation tools.
 */
public class BeatBuddyExtension extends ControllerExtension {
   private Application application;
   private Clip cursorClip;
   private Clip arrangerClip;

   private DocumentState documentState;
   public Setting patternTypeSetting;
   private Setting patternSelectorSetting;
   private Setting noteLengthSetting; // How long each note should be
   private Setting stepSizSetting;
   private Setting stepSizSubdivisionSetting;
   private Setting noteDestinationSetting;
   private Setting noteOctaveSetting;
   private Setting noteChannelSetting;
   private Setting toggleLauncherArrangerSetting;
   private Setting autoResizeLoopLengthSetting;
   private Setting autoReversePatternSetting;
   private Setting moveRotateStepsSetting;
   private NoteDestinationSettings noteDestSettings;

   private Setting spacer1;
   private Setting spacer2;
   private Setting spacer3;
   private Setting spacer4;

   private BeatBuddyPreferences preferences;

   protected BeatBuddyExtension(final BeatBuddyExtensionDefinition definition, final ControllerHost host) {
      super(definition, host);
   }

   @Override
   public void init() {
      final ControllerHost host = getHost();
      preferences = new BeatBuddyPreferences(host);
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

      initMoveStepsSetting();

      initPatternSetting();

      initNoteDestinationSetting();

      initStepSizeSetting();

      initPostActionSetting();

      initClearClipSetting();

      // Initialize launcher/arranger toggle
      initToggleLauncherArrangerSetting();

      // Show a notification to confirm initialization
      PopupUtils.showPopup("BeatBuddy Initialized");

   }

   /**
    * Initializes step size and note length settings.
    * The step size setting determines the grid resolution for pattern generation,
    * while the note length setting controls the duration of generated notes.
    * Both settings are synchronized by default but can be adjusted independently.
    */
   private void initStepSizeSetting() {
      stepSizSetting = (Setting) documentState.getEnumSetting("Step Size", "Clip", Utils.STEPSIZE_OPTIONS, "1/16");

      stepSizSubdivisionSetting = (Setting) documentState.getEnumSetting("Subdivisions", "Clip",
            Utils.STEPSIZE_CATEGORY_OPTIONS, "Straight");

      // set the note length equal to the selected step size
      ((EnumValue) stepSizSetting).addValueObserver(newValue -> {

         // Set both note length and step size
         ((SettableEnumValue) stepSizSetting).set(newValue);
         ((SettableEnumValue) noteLengthSetting).set(newValue);
      });

      // Steps note length
      noteLengthSetting = (Setting) documentState.getEnumSetting("Note Length", "Clip", Utils.STEPSIZE_OPTIONS, "1/16");

      ((EnumValue) noteLengthSetting).addValueObserver(newValue -> {
         ((SettableEnumValue) noteLengthSetting).set(newValue);
      });
   }

   /**
    * Initializes note destination settings including note pitch, octave, and MIDI channel.
    * These settings determine where the generated notes will be placed in terms of:
    * - Note pitch (C, C#, D, etc.)
    * - Octave (-2 to 8)
    * - MIDI channel (1-16)
    */
   private void initNoteDestinationSetting() {
      // Note destination dropdown
      String[] NOTEDESTINATION_OPTIONS = Utils.NOTE_NAMES;
      noteDestinationSetting = (Setting) documentState.getEnumSetting("Note Destination", "Note Destination",
            NOTEDESTINATION_OPTIONS,
            NOTEDESTINATION_OPTIONS[0]);

      // Note OCT destination dropdown
      String[] OCTAVEDESTINATION_OPTIONS = Arrays.stream(Utils.NOTE_OCTAVES)
            .mapToObj(String::valueOf)
            .toArray(String[]::new);
      noteOctaveSetting = (Setting) documentState.getEnumSetting("Note Octave", "Note Destination",
            OCTAVEDESTINATION_OPTIONS,
            OCTAVEDESTINATION_OPTIONS[3]);

      noteChannelSetting = (Setting) documentState.getNumberSetting("Note Channel", "Note Destination", 1, 16, 1,
            "Channel MIDI", 1);

      // Initialize NoteDestinationSettings
      noteDestSettings = new NoteDestinationSettings(getHost(), noteChannelSetting, NOTEDESTINATION_OPTIONS[0], 3);

      ((EnumValue) noteDestinationSetting).addValueObserver(newValue -> {
         noteDestSettings.setCurrentNote(newValue);
      });

      ((EnumValue) noteOctaveSetting).addValueObserver(newValue -> {
         noteDestSettings.setCurrentOctave(Integer.parseInt(newValue));
      });

      // Empty string for spacing
      spacer2 = (Setting) documentState.getStringSetting("----", "Clip", 0,
            "---------------------------------------------------");
      spacer2.disable();
      // Pattern step size
   }

   /**
    * Initializes pattern generation settings.
    * Includes:
    * - Generate button to trigger pattern creation
    * - Pattern selection dropdown with predefined patterns
    * - Pattern direction control (normal/reverse)
    */
   private void initPatternSetting() {
      // Generate button
      Signal generateButton = documentState.getSignalSetting("Generate!", "Generate", "Generate!");

      generateButton.addSignalObserver(() -> {
         generateDrumPattern();
      });

      // Pattern type selector
      patternTypeSetting = (Setting) documentState.getEnumSetting("Pattern Type", "Generate",
            new String[] { "Presets", "Random", "Custom" }, "Presets");

      ((EnumValue) patternTypeSetting).addValueObserver(newValue -> {
         if (newValue.equals("Presets")) {
            patternSelectorSetting.enable();
         } else {
            patternSelectorSetting.disable();
         }
      });

      // Define pattern settings
      final String[] PATTERN_OPTIONS = Arrays.stream(DrumPatterns.patterns)
            .map(pattern -> pattern[0].toString())
            .toArray(String[]::new);
      patternSelectorSetting = (Setting) documentState.getEnumSetting("Pattern", "Generate", PATTERN_OPTIONS,
            "Kick: Four on the Floor");
      ((EnumValue) patternSelectorSetting).addValueObserver(newValue -> {
         PopupUtils.showPopup(newValue.toString());
      });

      autoReversePatternSetting = (Setting) documentState.getEnumSetting("Reverse Pattern", "Generate",
            new String[] { "Normal", "Reverse" }, "Normal");

      // Empty string for spacing
      spacer1 = (Setting) documentState.getStringSetting("----", "Generate", 0,
            "---------------------------------------------------");
      spacer1.disable();
   }

   /**
    * Initializes step movement controls.
    * Provides functionality to move or rotate the pattern forward or backward.
    * Supports two modes:
    * - Move: shifts pattern and adds empty space
    * - Rotate: shifts pattern in a circular manner
    */
   private void initMoveStepsSetting() {
      moveRotateStepsSetting = (Setting) documentState.getEnumSetting("Move/Rotate", "Move Steps",
            new String[] { "Move", "Rotate" }, "Move");

      Signal moveFwd = documentState.getSignalSetting("Move Steps Forward", "Move Steps",
            ">>>");
      moveFwd.addSignalObserver(() -> {
         moveSteps(1);

      });

      Signal moveBwd = documentState.getSignalSetting("Move Steps Backward", "Move Steps",
            "<<<");
      moveBwd.addSignalObserver(() -> {
         moveSteps(-1);

      });

      // Empty string for spacing
      Setting spacerMoverSetting = (Setting) documentState.getStringSetting("----", "Move Steps", 0,
            "---------------------------------------------------");
      spacerMoverSetting.disable();
   }

   /**
    * Initializes post-generation action settings.
    * Currently supports automatic loop length adjustment after pattern generation.
    */
   private void initPostActionSetting() {
      // Empty string for spacing
      spacer3 = (Setting) documentState.getStringSetting("----", "Post Actions", 0,
            "---------------------------------------------------");
      spacer3.disable();

      autoResizeLoopLengthSetting = (Setting) documentState.getEnumSetting("Auto resize loop length", "Post Actions",
            new String[] { "Off", "On" }, "On");
   }

   /**
    * Initializes clip clearing functionality.
    * Provides a button to remove all notes from the current clip.
    */
   private void initClearClipSetting() {
      // Empty string for spacing
      spacer4 = (Setting) documentState.getStringSetting("----", "Clear Clip", 0,
            "---------------------------------------------------");
      spacer4.disable();

      // Clear current clip
      documentState.getSignalSetting("Clear current clip", "Clear Clip", "Clear current clip").addSignalObserver(() -> {
         getLauncherOrArrangerAsClip().clearSteps();
      });
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
   private void generateDrumPattern() {
      Clip clip = getLauncherOrArrangerAsClip();
      DrumPatternGenerator.generatePattern(clip, noteLengthSetting, stepSizSubdivisionSetting, 
            stepSizSetting, noteDestSettings, patternSelectorSetting, patternTypeSetting,
            autoReversePatternSetting, autoResizeLoopLengthSetting);
   }

   /**
    * Returns the currently active clip based on the launcher/arranger toggle setting.
    * 
    * @return The active Clip object (either launcher or arranger clip)
    */
   private Clip getLauncherOrArrangerAsClip() {
      return ClipUtils.getLauncherOrArrangerAsClip(toggleLauncherArrangerSetting, arrangerClip, cursorClip);
   }

   /**
    * Moves or rotates steps in the current pattern.
    * 
    * @param stepOffset The number of steps to move/rotate (positive for forward, negative for backward)
    */
   public void moveSteps(int stepOffset) {
      Clip clip = getLauncherOrArrangerAsClip();
      int channel = noteDestSettings.getCurrentChannelAsInt();
      int noteDestination = noteDestSettings.getCurrentNoteDestinationAsInt();
      String stepSize = ((EnumValue) stepSizSetting).get();
      String subdivision = ((EnumValue) stepSizSubdivisionSetting).get();
      boolean isRotate = ((EnumValue) moveRotateStepsSetting).get().equals("Rotate");

      ClipUtils.handleStepMovement(clip, channel, noteDestination, stepSize, subdivision, stepOffset, isRotate);
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
}
