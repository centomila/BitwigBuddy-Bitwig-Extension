package com.centomila;

import static com.centomila.utils.SettingsHelper.disableSetting;
import static com.centomila.utils.SettingsHelper.enableSetting;
import static com.centomila.utils.SettingsHelper.showSetting;

import java.util.Random;

import static com.centomila.utils.SettingsHelper.createEnumSetting;

import com.bitwig.extension.controller.api.DocumentState;
import com.bitwig.extension.controller.api.EnumValue;
import com.bitwig.extension.controller.api.SettableStringValue;
import com.bitwig.extension.controller.api.SettableEnumValue;
import com.bitwig.extension.controller.api.Setting;
import com.bitwig.extension.controller.api.Signal;
import com.centomila.utils.PopupUtils;

public class ModeSelectSettings {

    static Setting modeSelectSetting;
    static Setting toggleLauncherArrangerSetting;

    public static void init(BitwigBuddyExtension extension) {

        initToggleModesSettings();
        initiObservers();
    }

    /**
     * Initializes the clip destination toggle.
     * Allows switching between launcher and arranger clip modes,
     * determining where patterns will be generated.
     */
    private static void initToggleModesSettings() {
        // Mode select setting
        final String[] MODE_SELECT_OPTIONS = new String[] { "Generate", "Edit" };

        modeSelectSetting = (Setting) createEnumSetting("Mode Select", "Z",
                MODE_SELECT_OPTIONS,
                MODE_SELECT_OPTIONS[0]);

        // Launcher/Arranger toggle
        final String[] TOGGLE_LAUNCHER_ARRANGER_OPTIONS = new String[] { "Launcher", "Arranger", };

        toggleLauncherArrangerSetting = (Setting) createEnumSetting("Destination Launcher/Arranger", "Z",
                TOGGLE_LAUNCHER_ARRANGER_OPTIONS,
                TOGGLE_LAUNCHER_ARRANGER_OPTIONS[0]);

    }

    private static void initiObservers() {
        ((EnumValue) modeSelectSetting).addValueObserver(newValue -> {
            PopupUtils.showPopup("Mode: " + newValue);
            toggleMode(newValue);
        });

        ((EnumValue) toggleLauncherArrangerSetting).addValueObserver(newValue -> {
            PopupUtils.showPopup("Destination: " + newValue);
            if (newValue.equals("Arranger")) {
                disableSetting(PostActionSettings.duplicateClipSetting);
            } else {
                enableSetting(PostActionSettings.duplicateClipSetting);
            }
        });

    }

    private static void toggleMode(String newValue) {
        String currentMode = newValue;
        if (currentMode.equals("Generate")) {
            // ((SettableEnumValue) modeSelectSetting).set("Edit");
            gotoGenerateMode();
        } else {
            // ((SettableEnumValue) modeSelectSetting).set("Generate");
            gotoEditMode();
        }
    }

    private static void gotoEditMode() {
        // Hide generate settings
        // RandomPattern.hideSettings();
        // for each Setting in RandomPattern.allSettings, hideSetting(setting);

        for (Setting setting : RandomPattern.allSettings) {
            setting.hide();
        }

        for (Setting setting : MoveStepsHandler.allSettings) {
            setting.show();
        }

        for (Setting setting : PatternSettings.allSettings) {
            setting.hide();
        }

        for (Setting setting : PostActionSettings.allSettings) {
            setting.hide();
        }

        // StepSizeSettings.hideSettings();
        // NoteDestinationSettings.hideSettings();
        // VelocityShapeSettings.hideSettings();
        // PostActionSettings.hideSettings();
    }

    // GENERATE MODE
    private static void gotoGenerateMode() {
        // Show generate settings
        // RandomPattern.showSettings();
        // for each Setting in RandomPattern.allSettings, showSetting(setting);
        // if presetPatternType is random

        // for each Setting in MoveStepsHandler.allSettings, showSetting(setting);
        for (Setting setting : MoveStepsHandler.allSettings) {
            setting.hide();
        }

        // hide PatternSettings
        for (Setting setting : PatternSettings.allSettings) {
            setting.show();
        }

        PatternSettings.generatorTypeSelector(((EnumValue) PatternSettings.patternTypeSetting).get());

        // StepSizeSettings.showSettings();
        // NoteDestinationSettings.showSettings();
        // VelocityShapeSettings.showSettings();

        showSetting(PostActionSettings.allSettings);
       PostActionSettings.showPostActionsSettings();
    }

}
