package com.centomila;

import static com.centomila.utils.PopupUtils.*;

import com.bitwig.extension.controller.api.Clip;
import com.bitwig.extension.controller.api.DocumentState;
import com.bitwig.extension.controller.api.EnumValue;
import com.bitwig.extension.controller.api.Setting;
import com.bitwig.extension.controller.api.Signal;
import com.centomila.utils.SettingsHelper;

/**
 * Handles the movement and rotation of steps in a Bitwig clip.
 * This class manages settings and signals for step manipulation operations.
 */
public class MoveStepsHandler {
    private static final String CATEGORY_MOVE_STEPS = "2 Move Steps";
    private static final String[] MOVE_MODES = { "Move", "Rotate" };
    private static final String DEFAULT_MOVE_MODE = MOVE_MODES[0];
    private static final String ERROR_NO_CLIP = "No clip selected";
    private static final String ERROR_INVALID_STEP = "Invalid step offset";
    private static final String ERROR_INVALID_SETTINGS = "Invalid step size or subdivision settings";
    private static final int MAX_STEP_OFFSET = 128;

    private final BitwigBuddyExtension extension;
    private Setting moveRotateStepsSetting;
    private Setting moveFwd;
    private Setting moveBwd;
    private Setting spacerMoveSteps;
    public static Setting[] allSettings;

    /**
     * Creates a new MoveStepsHandler instance.
     * 
     * @param extension The parent BitwigBuddy extension
     * @throws IllegalArgumentException if extension is null
     */
    public MoveStepsHandler(BitwigBuddyExtension extension) {
        if (extension == null) {
            throw new IllegalArgumentException("Extension cannot be null");
        }
        this.extension = extension;
    }

    /**
     * Initializes the handler with document state settings and signals.
     * 
     * @param documentState The document state to initialize with
     * @throws IllegalArgumentException if documentState is null
     */
    public void init(DocumentState documentState) {
        if (documentState == null) {
            throw new IllegalArgumentException("DocumentState cannot be null");
        }
        // Initialize SettingsHelper with the extension

        initializeMoveRotateSetting(documentState);
        initializeMoveSignals(documentState);
    }

    private void initializeMoveRotateSetting(DocumentState documentState) {
        // Spacer
        spacerMoveSteps = (Setting) SettingsHelper.createStringSetting(
                SettingsHelper.titleWithLine("MOVE / ROTATE STEPS -------------------"),
                CATEGORY_MOVE_STEPS,
                0,
                "---------------------------------------------------");
        SettingsHelper.disableSetting(spacerMoveSteps);
        // Replace direct call with SettingsHelper
        moveRotateStepsSetting = (Setting) SettingsHelper.createEnumSetting(
                "Move / Rotate",
                CATEGORY_MOVE_STEPS,
                MOVE_MODES,
                DEFAULT_MOVE_MODE);
    }

    private void initializeMoveSignals(DocumentState documentState) {

        // Use SettingsHelper to create signal settings
        moveFwd = (Setting) SettingsHelper.createSignalSetting(
                "Move Steps Forward",
                CATEGORY_MOVE_STEPS,
                ">>>");
        moveBwd = (Setting) SettingsHelper.createSignalSetting(
                "Move Steps Backward",
                CATEGORY_MOVE_STEPS,
                "<<<");

        allSettings = new Setting[] { (Setting) spacerMoveSteps, (Setting) moveRotateStepsSetting, (Setting) moveFwd,
                (Setting) moveBwd };

        ((Signal) moveFwd).addSignalObserver(() -> handleStepMovement(1));
        ((Signal) moveBwd).addSignalObserver(() -> handleStepMovement(-1));
    }

    /**
     * Handles the movement or rotation of steps in the clip.
     * 
     * @param stepOffset The number of steps to move (positive for forward, negative
     *                   for backward)
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
            int channel = NoteDestinationSettings.getCurrentChannelAsInt();
            int noteDestination = NoteDestinationSettings.getCurrentNoteDestinationAsInt();

            EnumValue stepSizeSetting = (EnumValue) StepSizeSettings.stepSizSetting;
            EnumValue subdivisionSetting = (EnumValue) StepSizeSettings.stepSizSubdivisionSetting;

            if (stepSizeSetting == null || subdivisionSetting == null) {
                showPopup(ERROR_INVALID_SETTINGS);
                return;
            }

            String stepSize = stepSizeSetting.get();
            String subdivision = subdivisionSetting.get();
            boolean isRotate = ((EnumValue) moveRotateStepsSetting).get().equals(MOVE_MODES[1]);

            ClipUtils.handleStepMovement(
                    clip,
                    channel,
                    noteDestination,
                    stepSize,
                    subdivision,
                    stepOffset,
                    isRotate);
        } catch (ClassCastException e) {
            extension.getHost().errorln("Settings type error: " + e.getMessage());
            showPopup(ERROR_INVALID_SETTINGS);
        } catch (Exception e) {
            extension.getHost().errorln("Failed to move steps: " + e.getMessage());
            showPopup("Error: " + e.getMessage());
        }
    }

    // Show / Hide Settings
    public static void showAllSettings() {
        SettingsHelper.showSetting(allSettings);
    }

    public static void hideAllSettings() {
        SettingsHelper.hideSetting(allSettings);
    }
}
