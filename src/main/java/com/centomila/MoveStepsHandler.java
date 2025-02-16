package com.centomila;

import com.bitwig.extension.controller.api.Clip;
import com.bitwig.extension.controller.api.DocumentState;
import com.bitwig.extension.controller.api.EnumValue;
import com.bitwig.extension.controller.api.Signal;
import static com.centomila.utils.PopupUtils.*;

/**
 * Handles the movement and rotation of steps in a Bitwig clip.
 * This class manages settings and signals for step manipulation operations.
 */
public class MoveStepsHandler {
    private static final String CATEGORY_MOVE_STEPS = "Move Steps";
    private static final String[] MOVE_MODES = { "Move", "Rotate" };
    private static final String DEFAULT_MOVE_MODE = MOVE_MODES[0];
    private static final String ERROR_NO_CLIP = "No clip selected";
    private static final String ERROR_INVALID_STEP = "Invalid step offset";
    private static final String ERROR_INVALID_SETTINGS = "Invalid step size or subdivision settings";
    private static final int MAX_STEP_OFFSET = 128;
    
    private final BeatBuddyExtension extension;
    private EnumValue moveRotateStepsSetting;

    /**
     * Creates a new MoveStepsHandler instance.
     * @param extension The parent BeatBuddy extension
     * @throws IllegalArgumentException if extension is null
     */
    public MoveStepsHandler(BeatBuddyExtension extension) {
        if (extension == null) {
            throw new IllegalArgumentException("Extension cannot be null");
        }
        this.extension = extension;
    }

    /**
     * Initializes the handler with document state settings and signals.
     * @param documentState The document state to initialize with
     * @throws IllegalArgumentException if documentState is null
     */
    public void init(DocumentState documentState) {
        if (documentState == null) {
            throw new IllegalArgumentException("DocumentState cannot be null");
        }
        
        initializeMoveRotateSetting(documentState);
        initializeMoveSignals(documentState);
    }

    private void initializeMoveRotateSetting(DocumentState documentState) {
        moveRotateStepsSetting = documentState.getEnumSetting(
            "Move/Rotate", 
            CATEGORY_MOVE_STEPS,
            MOVE_MODES, 
            DEFAULT_MOVE_MODE
        );
    }

    private void initializeMoveSignals(DocumentState documentState) {
        Signal moveFwd = documentState.getSignalSetting(
            "Move Steps Forward", 
            CATEGORY_MOVE_STEPS, 
            ">>>"
        );
        Signal moveBwd = documentState.getSignalSetting(
            "Move Steps Backward", 
            CATEGORY_MOVE_STEPS, 
            "<<<"
        );

        moveFwd.addSignalObserver(() -> handleStepMovement(1));
        moveBwd.addSignalObserver(() -> handleStepMovement(-1));
    }

    /**
     * Handles the movement or rotation of steps in the clip.
     * @param stepOffset The number of steps to move (positive for forward, negative for backward)
     * @throws IllegalArgumentException if stepOffset is invalid
     */
    private void handleStepMovement(int stepOffset) {
        if (Math.abs(stepOffset) > MAX_STEP_OFFSET) {
            showPopup(ERROR_INVALID_STEP);
            return;
        }

        Clip clip = extension.getLauncherOrArrangerAsClip();
        if (clip == null) {
            showPopup(ERROR_NO_CLIP);
            return;
        }

        try {
            int channel = ((NoteDestinationSettings) extension.noteDestSettings).getCurrentChannelAsInt();
            int noteDestination = ((NoteDestinationSettings) extension.noteDestSettings).getCurrentNoteDestinationAsInt();
            
            EnumValue stepSizeSetting = (EnumValue) extension.stepSizSetting;
            EnumValue subdivisionSetting = (EnumValue) extension.stepSizSubdivisionSetting;
            
            if (stepSizeSetting == null || subdivisionSetting == null) {
                showPopup(ERROR_INVALID_SETTINGS);
                return;
            }

            String stepSize = stepSizeSetting.get();
            String subdivision = subdivisionSetting.get();
            boolean isRotate = moveRotateStepsSetting.get().equals(MOVE_MODES[1]);

            ClipUtils.handleStepMovement(
                clip, 
                channel, 
                noteDestination, 
                stepSize, 
                subdivision, 
                stepOffset, 
                isRotate
            );
        } catch (ClassCastException e) {
            extension.getHost().errorln("Settings type error: " + e.getMessage());
            showPopup(ERROR_INVALID_SETTINGS);
        } catch (Exception e) {
            extension.getHost().errorln("Failed to move steps: " + e.getMessage());
            showPopup("Error: " + e.getMessage());
        }
    }
}
