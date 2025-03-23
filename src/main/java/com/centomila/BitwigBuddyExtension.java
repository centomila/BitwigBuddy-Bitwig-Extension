package com.centomila;

import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.CueMarker;
import com.bitwig.extension.controller.api.CueMarkerBank;
import com.bitwig.extension.controller.api.CursorChannel;
import com.bitwig.extension.controller.api.CursorRemoteControlsPage;
import com.bitwig.extension.controller.api.CursorTrack;
import com.bitwig.extension.controller.api.DeviceBank;

import static com.centomila.utils.PopupUtils.showPopup;

import com.bitwig.extension.controller.ControllerExtension;
import com.bitwig.extension.controller.api.Application;
import com.bitwig.extension.controller.api.Arranger;
import com.bitwig.extension.controller.api.Clip;
import com.bitwig.extension.controller.api.ClipLauncherSlot;
import com.bitwig.extension.controller.api.DocumentState;
import com.bitwig.extension.controller.api.DrumPadBank;
import com.bitwig.extension.controller.api.PinnableCursorDevice;
import com.bitwig.extension.controller.api.Project;
import com.bitwig.extension.controller.api.RemoteControl;
import com.bitwig.extension.controller.api.SceneBank;
import com.bitwig.extension.controller.api.TimeSignatureValue;
import com.bitwig.extension.controller.api.TrackBank;
import com.bitwig.extension.controller.api.Transport;
import com.bitwig.extension.controller.api.Channel;
import com.bitwig.extension.controller.api.Cursor;
import com.centomila.utils.PopupUtils;
import com.centomila.utils.SettingsHelper;
import com.centomila.utils.DeviceMatcherDrumMachine;
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
   public CursorTrack cursorTrack;
   public RemoteControl remoteControl;
   public CursorRemoteControlsPage cursorRemoteControlsPage;
   public DeviceBank deviceBank;
   public PinnableCursorDevice cursorDeviceSlot;
   public DrumPadBank drumPadBank;
   public Arranger arranger;
   public Transport transport;
   public Project project;
   public CueMarkerBank cueMarkerBank;
   public CueMarker cueMarker;
   public TimeSignatureValue timeSignature;
   public TrackBank trackBank;
   public ClipLauncherSlot clipLauncherSlot;
   public SceneBank sceneBank;
   public Channel channel;
   public CursorChannel cursorChannel;
   public Cursor cursor;

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
      this.application = host.createApplication();

      this.cursorClip = host.createLauncherCursorClip((16 * 8), 128);
      this.arrangerClip = host.createArrangerCursorClip(256, 128);
      // CursorTrack createCursorTrack(String id,
      // String name,
      // int numSends,
      // int numScenes,
      // boolean shouldFollowSelection)
      this.cursorTrack = host.createCursorTrack("cursorTrack", "Cursor Track", 0, 128, true);
      this.documentState = host.getDocumentState();
      this.transport = host.createTransport();
      this.clipLauncherSlot = cursorClip.clipLauncherSlot();
      this.timeSignature = transport.timeSignature();
      this.sceneBank = host.createSceneBank(128);
      this.arranger = host.createArranger();
      this.project = host.getProject();
      this.cursorDeviceSlot = cursorTrack.createCursorDevice();

      this.timeSignature.subscribe();
      this.sceneBank.subscribe();
      this.cursorClip.subscribe();
      this.arrangerClip.subscribe();
      this.cursorTrack.subscribe();
      this.transport.subscribe();
      this.clipLauncherSlot.subscribe();

      this.application.recordQuantizationGrid().markInterested();
      // Arranger
      this.arranger.isClipLauncherVisible().markInterested();
      this.arranger.areCueMarkersVisible().markInterested();
      this.arranger.isPlaybackFollowEnabled().markInterested();
      this.arranger.isTimelineVisible().markInterested();
      this.arranger.hasDoubleRowTrackHeight().markInterested();
      // Device Matcher
      this.trackBank = host.createTrackBank(128, 0, 128); // Evaluate to change to createMainTrackBank in the future
      this.deviceBank = this.cursorTrack.createDeviceBank(128);
      this.drumPadBank = deviceBank.getDevice(0).createDrumPadBank(128);
      this.drumPadBank.scrollPosition().set(0);

      this.cursorDeviceSlot.exists().markInterested();
      this.cursorDeviceSlot.isWindowOpen().markInterested();
      this.cursorDeviceSlot.isEnabled().markInterested();
      this.cursorDeviceSlot.isPinned().markInterested();
      this.cursorDeviceSlot.name().markInterested();
      this.cursorDeviceSlot.isExpanded().markInterested();

      // Remote Controls
      this.cursorRemoteControlsPage = this.cursorDeviceSlot.createCursorRemoteControlsPage(1);
      this.cursorRemoteControlsPage.getParameter(0).markInterested();
      this.remoteControl = cursorRemoteControlsPage.getParameter(0);

      // This makes sure the track bank tracks the selected track in Bitwig
      this.trackBank.followCursorTrack(cursorTrack);
      this.trackBank.setShouldShowClipLauncherFeedback(true);
      this.sceneBank.setIndication(true);

      // Mark interested for all properties
      this.cursorTrack.name().markInterested();
      this.cursorTrack.color().markInterested();
      this.cursorTrack.getIsPreFader().markInterested();
      this.cursorTrack.clipLauncherSlotBank().cursorIndex().markInterested();
      this.cursorTrack.clipLauncherSlotBank().scrollPosition().markInterested();
      this.cursorTrack.clipLauncherSlotBank().itemCount().markInterested();
      this.cursorTrack.clipLauncherSlotBank().exists().markInterested();
      this.cursorTrack.volume().markInterested();

      this.remoteControl.markInterested();

      this.application.displayProfile().markInterested();

      this.deviceBank.getDeviceChain().name().markInterested();
      this.deviceBank.getDeviceChain().exists().markInterested();
      this.deviceBank.cursorIndex().markInterested();
      this.deviceBank.scrollPosition().markInterested();
      this.deviceBank.itemCount().markInterested();
      this.deviceBank.exists().markInterested();

      this.cueMarkerBank = arranger.createCueMarkerBank(128);
      this.transport.playStartPosition().markInterested();
      this.transport.playStartPositionInSeconds().markInterested();
      this.transport.playPosition().markInterested();
      this.transport.playPositionInSeconds().markInterested();
      this.transport.getPosition().markInterested();
      this.transport.isPlaying().markInterested();
      this.transport.isPunchInEnabled().markInterested();
      this.transport.isPunchOutEnabled().markInterested();

      this.transport.isArrangerLoopEnabled().markInterested();
      this.transport.isClipLauncherOverdubEnabled().markInterested();
      this.transport.isMetronomeEnabled().markInterested();
      this.transport.tempo().markInterested();
      this.transport.timeSignature().markInterested();

      this.trackBank.cursorIndex().markInterested();
      this.trackBank.channelCount().markInterested();
      this.trackBank.scrollPosition().markInterested();
      this.trackBank.itemCount().markInterested();
      this.trackBank.cursorIndex().markInterested();
      this.trackBank.channelCount().markInterested();
      this.trackBank.sceneBank().cursorIndex().markInterested();
      this.trackBank.sceneBank().scrollPosition().markInterested();
      this.trackBank.sceneBank().itemCount().markInterested();

      this.sceneBank.cursorIndex().markInterested();
      this.sceneBank.scrollPosition().markInterested();

      this.drumPadBank.scrollPosition().markInterested();
      this.drumPadBank.itemCount().markInterested();
      this.drumPadBank.cursorIndex().markInterested();
      this.drumPadBank.exists().markInterested();

      this.cueMarkerBank.subscribe();
      for (int i = 0; i < 128; i++) {
         this.cueMarkerBank.getItemAt(i).name().markInterested();
         this.cueMarkerBank.getItemAt(i).getColor().markInterested();
         this.cueMarkerBank.getItemAt(i).exists().markInterested();
         this.cueMarkerBank.getItemAt(i).position().markInterested();

         this.trackBank.getItemAt(i).name().markInterested();
         this.trackBank.getItemAt(i).color().markInterested();
         this.trackBank.getItemAt(i).exists().markInterested();
         this.trackBank.getItemAt(i).position().markInterested();
         this.trackBank.getItemAt(i).mute().markInterested();
         this.trackBank.getItemAt(i).solo().markInterested();
         this.trackBank.getItemAt(i).arm().markInterested();
         this.trackBank.getItemAt(i).volume().markInterested();
         this.trackBank.getItemAt(i).pan().markInterested();

         this.trackBank.getItemAt(i).clipLauncherSlotBank().cursorIndex().markInterested();
         this.trackBank.getItemAt(i).clipLauncherSlotBank().scrollPosition().markInterested();
         this.trackBank.getItemAt(i).clipLauncherSlotBank().itemCount().markInterested();
         this.trackBank.getItemAt(i).clipLauncherSlotBank().exists().markInterested();

         this.deviceBank.getItemAt(i).name().markInterested();
         this.deviceBank.getItemAt(i).exists().markInterested();
         this.deviceBank.getItemAt(i).position().markInterested();
         this.deviceBank.getItemAt(i).getCursorSlot().name().markInterested();
         this.deviceBank.getItemAt(i).getCursorSlot().exists().markInterested();

         this.drumPadBank.getItemAt(i).name().markInterested();
         this.drumPadBank.getItemAt(i).exists().markInterested();
         this.drumPadBank.getItemAt(i).color().markInterested();

         this.sceneBank.getItemAt(i).name().markInterested();
         this.sceneBank.getItemAt(i).color().markInterested();
         this.sceneBank.getItemAt(i).exists().markInterested();
         this.sceneBank.getItemAt(i).clipCount().markInterested();
         this.sceneBank.getItemAt(i).exists().markInterested();
         this.sceneBank.getItemAt(i).sceneIndex().markInterested();

         this.channel = this.trackBank.getItemAt(i);
      }

      this.cueMarkerBank.scrollPosition().markInterested();
      this.cueMarkerBank.itemCount().markInterested();

      this.application.panelLayout().markInterested();

      this.cursorClip.exists().markInterested();
      this.cursorClip.getLoopLength().markInterested();
      this.cursorClip.getLoopStart().markInterested();
      this.cursorClip.getPlayStart().markInterested();
      this.cursorClip.getPlayStop().markInterested();

      this.arrangerClip.getLoopLength().markInterested();
      this.arrangerClip.getLoopStart().markInterested();
      this.arrangerClip.getPlayStart().markInterested();
      this.arrangerClip.getPlayStop().markInterested();
      this.arrangerClip.clipLauncherSlot().isPlaying().markInterested();

      this.clipLauncherSlot.isPlaybackQueued().markInterested();
      this.clipLauncherSlot.name().markInterested();
      this.clipLauncherSlot.color().markInterested();
      this.clipLauncherSlot.exists().markInterested();
      this.clipLauncherSlot.hasContent().markInterested();
      this.clipLauncherSlot.sceneIndex().markInterested();
      this.clipLauncherSlot.isRecording().markInterested();
      this.clipLauncherSlot.isRecordingQueued().markInterested();
      this.clipLauncherSlot.isStopQueued().markInterested();
      this.clipLauncherSlot.isPlaybackQueued().markInterested();

      // Initialize settings
      SettingsHelper.init(this);

      // Initialize settings in correct order for GUI layout
      ModeSelectSettings.init(this);
      moveStepsHandler = new MoveStepsHandler(this);
      moveStepsHandler.init(documentState);
      PatternSettings.init(this);
      EditClipSettings.init(this);
      MacroActionSettings.init(this);
      MacroActionSettings.hideAllSettings();
      ProgramPattern.init(this);
      NoteDestinationSettings.init(this);
      StepSizeSettings.init(this);
      PostActionSettings.init(this);
      ModeSelectSettings.hideMacroSettings();
      ClipUtils.init(this);

      DeviceMatcherDrumMachine.initializeDeviceMatcherDM(this, host);

      // Initialize save section last to position it at the bottom of the GUI
      CustomPresetSaver.initCustomSavePresetSetting(documentState, this);

      // After all settings are initialized, set initial visibility based on current
      // mode
      String patternType = PatternSettings.getPatternType();
      if (patternType != null) {
         PatternSettings.generatorTypeSelector(patternType);
      }

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
      return ClipUtils.getLauncherOrArrangerAsClip(arrangerClip, cursorClip);
   }

   public String getRecordQuantizationGridSize() {
      return this.application.recordQuantizationGrid().get();
   }

   public void setRecordQuantizationGridSize(String size) {
      this.application.recordQuantizationGrid().set(size);
   }

   public String getSnapSizeGrid() {
      return this.timeSignature.get();
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
