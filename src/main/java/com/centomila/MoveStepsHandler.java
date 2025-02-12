package com.centomila;

import com.bitwig.extension.controller.api.Clip;
import com.bitwig.extension.controller.api.DocumentState;
import com.bitwig.extension.controller.api.EnumValue;
import com.bitwig.extension.controller.api.Setting;
import com.bitwig.extension.controller.api.Signal;

public class MoveStepsHandler {
    private final BeatBuddyExtension extension;
    private Setting moveRotateStepsSetting;

    public MoveStepsHandler(BeatBuddyExtension extension) {
        this.extension = extension;
    }

    public void init(DocumentState documentState) {
        setupMoveRotateSetting(documentState);
        setupMoveSignals(documentState);
        disableSpacerSetting(documentState);
    }

    private void setupMoveRotateSetting(DocumentState documentState) {
        moveRotateStepsSetting = (Setting) documentState.getEnumSetting(
            "Move/Rotate", 
            "Move Steps",
            new String[] { "Move", "Rotate" }, 
            "Move"
        );
    }

    private void setupMoveSignals(DocumentState documentState) {
        Signal moveFwd = documentState.getSignalSetting("Move Steps Forward", "Move Steps", ">>>");
        Signal moveBwd = documentState.getSignalSetting("Move Steps Backward", "Move Steps", "<<<");

        moveFwd.addSignalObserver(() -> moveSteps(1));
        moveBwd.addSignalObserver(() -> moveSteps(-1));
    }

    private void disableSpacerSetting(DocumentState documentState) {
        Setting spacerMoverSetting = (Setting) documentState.getStringSetting(
            "----", 
            "Move Steps", 
            0,
            "---------------------------------------------------"
        );
        spacerMoverSetting.disable();
    }

    public void moveSteps(int stepOffset) {
        Clip clip = extension.getLauncherOrArrangerAsClip();
        int channel = extension.getNoteDestSettings().getCurrentChannelAsInt();
        int noteDestination = extension.getNoteDestSettings().getCurrentNoteDestinationAsInt();

        // Retrieve step size and subdivision settings
        String stepSize = ((EnumValue) extension.getStepSizSetting()).get();
        String subdivision = ((EnumValue) extension.getStepSizSubdivisionSetting()).get();
        boolean isRotate = ((EnumValue) moveRotateStepsSetting).get().equals("Rotate");

        ClipUtils.handleStepMovement(clip, channel, noteDestination, stepSize, subdivision, stepOffset, isRotate);
    }
}
