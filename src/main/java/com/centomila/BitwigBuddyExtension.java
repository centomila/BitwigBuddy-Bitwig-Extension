package com.centomila;

import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.CueMarker;
import com.bitwig.extension.controller.api.CueMarkerBank;
import com.bitwig.extension.controller.api.CursorTrack;
import com.bitwig.extension.controller.ControllerExtension;
import com.bitwig.extension.controller.api.Application;
import com.bitwig.extension.controller.api.Arranger;
import com.bitwig.extension.controller.api.Clip;
import com.bitwig.extension.controller.api.ClipLauncherSlot;
import com.bitwig.extension.controller.api.ClipLauncherSlotBank;
import com.bitwig.extension.controller.api.DocumentState;
import com.bitwig.extension.controller.api.Project;
import com.bitwig.extension.controller.api.TimeSignatureValue;
import com.bitwig.extension.controller.api.Track;
import com.bitwig.extension.controller.api.TrackBank;
import com.bitwig.extension.controller.api.Transport;
import com.bitwig.extension.controller.api.Channel;
import com.bitwig.extension.controller.api.ChannelBank;
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
   public CursorTrack cursorTrack;
   public Arranger arranger;
   public Transport transport;
   public Project project;
   public CueMarkerBank cueMarkerBank;
   public CueMarker cueMarker;
   public TimeSignatureValue timeSignature;
   public TrackBank trackBank;
   public ClipLauncherSlot clipLauncherSlot;
   public ClipLauncherSlotBank clipLauncherSlotBank;
   public Channel channel;

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
      this.arrangerClip = host.createArrangerCursorClip((16 * 8), 128);
      this.cursorTrack = host.createCursorTrack("BB_CURSOR_TRACK", "BB Cursor Track", 0, 0, true);
      this.documentState = host.getDocumentState();
      this.transport = host.createTransport();
      this.arranger = host.createArranger();
      this.project = host.getProject();
      this.clipLauncherSlot = cursorClip.clipLauncherSlot();
      this.timeSignature = transport.timeSignature();

      this.trackBank = host.createTrackBank(128, 0, 128);

      // This makes sure the track bank tracks the selected track in Bitwig
      this.trackBank.followCursorTrack(cursorTrack);
      

      this.cueMarkerBank = arranger.createCueMarkerBank(128);

      this.trackBank.cursorIndex().markInterested();
      this.trackBank.channelCount().markInterested();
      this.trackBank.scrollPosition().markInterested();
      this.trackBank.itemCount().markInterested();

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

         


      }

      this.cueMarkerBank.scrollPosition().markInterested();
      this.cueMarkerBank.itemCount().markInterested();

      this.application.panelLayout().markInterested();

      this.cursorClip.getLoopLength().markInterested();
      this.cursorClip.getLoopStart().markInterested();
      this.cursorClip.getPlayStart().markInterested();
      this.cursorClip.getPlayStop().markInterested();
      this.cursorClip.clipLauncherSlot().isPlaying().markInterested();
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
      return ClipUtils.getLauncherOrArrangerAsClip(ModeSelectSettings.toggleLauncherArrangerSetting, arrangerClip,
            cursorClip);
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
