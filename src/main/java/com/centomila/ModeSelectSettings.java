package com.centomila;

import static com.centomila.utils.SettingsHelper.*;


import com.bitwig.extension.controller.api.EnumValue;
import com.bitwig.extension.controller.api.Setting;

public class ModeSelectSettings {
    private static final String CATEGORY_MODE_SELECT = "1 Mode Select";

    private static Setting spacerSelectModSetting;
    public static Setting modeGenerateEditToggleSetting;
    public static Setting toggleLauncherArrangerSetting;

    public static void init(BitwigBuddyExtension extension) {

        // Mode select setting
        final String[] MODE_SELECT_OPTIONS = new String[] { "Generate", "Edit", "Macro" };

        spacerSelectModSetting = (Setting) createStringSetting(titleWithLine("MODE SELECT"),
                CATEGORY_MODE_SELECT, 0,
                "---------------------------------------------------");
        disableSetting(spacerSelectModSetting);

        modeGenerateEditToggleSetting = (Setting) createEnumSetting("Generate/Edit Mode", CATEGORY_MODE_SELECT,
                MODE_SELECT_OPTIONS,
                MODE_SELECT_OPTIONS[0]);

        // Launcher/Arranger toggle
        final String[] TOGGLE_LAUNCHER_ARRANGER_OPTIONS = new String[] { "Launcher", "Arranger", };

        toggleLauncherArrangerSetting = (Setting) createEnumSetting("Destination Launcher/Arranger",
                CATEGORY_MODE_SELECT,
                TOGGLE_LAUNCHER_ARRANGER_OPTIONS,
                TOGGLE_LAUNCHER_ARRANGER_OPTIONS[0]);

        initToggleModeObservers();
    }

    private static void initToggleModeObservers() {
        ((EnumValue) modeGenerateEditToggleSetting).addValueObserver(newValue -> {
            // PopupUtils.showPopup("Mode: " + newValue);
            toggleMode(newValue);
        });

        ((EnumValue) toggleLauncherArrangerSetting).addValueObserver(newValue -> {
            // PopupUtils.showPopup("Destination: " + newValue);
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
        } else if (currentMode.equals("Edit")) {
            // ((SettableEnumValue) modeSelectSetting).set("Generate");
            gotoEditMode();
        } else if (currentMode.equals("Macro")) {
            // ((SettableEnumValue) modeSelectSetting).set("Generate");
            gotoMacroMode();
        }
    }

    private static void gotoEditMode() {
        // Hide generate settings
        // RandomPattern.hideSettings();
        // for each Setting in RandomPattern.allSettings, hideSetting(setting);

        for (Setting setting : ProgramPattern.allSettings) {
            setting.hide();
        }

        for (Setting setting : MoveStepsHandler.allSettings) {
            setting.hide();
        }

        for (Setting setting : PatternSettings.allSettings) {
            setting.hide();
        }

        for (Setting setting : PostActionSettings.allSettings) {
            setting.hide();
        }

        for (Setting setting : NoteDestinationSettings.allSettings) {
            setting.hide();
        }

        for (Setting setting : StepSizeSettings.allSettings) {
            setting.hide();
        }


        ProgramPattern.showProgramPatternSettings();
        

        EditClipSettings.showEditClipSettings();

        NoteDestinationSettings.noteDestinationSetting.hide();
        NoteDestinationSettings.noteChannelSetting.hide();
        ProgramPattern.programStepQtySetting.hide();
        ProgramPattern.programDensitySetting.hide();


        // StepSizeSettings.hideSettings();
        // NoteDestinationSettings.hideSettings();
        // VelocityShapeSettings.hideSettings();
        // PostActionSettings.hideSettings();
    }

    // GENERATE MODE
    public static void gotoGenerateMode() {
        // Show generate settings
        // RandomPattern.showSettings();
        // for each Setting in RandomPattern.allSettings, showSetting(setting);
        // if presetPatternType is random

        for (Setting setting : MoveStepsHandler.allSettings) {
            setting.show();
        }

        // hide PatternSettings
        for (Setting setting : PatternSettings.allSettings) {
            setting.show();
        }

        for (Setting setting : NoteDestinationSettings.allSettings) {
            setting.show();
        }

        for (Setting setting : StepSizeSettings.allSettings) {
            setting.show();
        }

        // get the value from GlobalPreferences of showChannelDestination
        if (!GlobalPreferences.showChannelDestinationPref.get()) {

            hideSetting(NoteDestinationSettings.noteChannelSetting);
        }

        PatternSettings.generatorTypeSelector(((EnumValue) PatternSettings.patternTypeSetting).get());

        // StepSizeSettings.showSettings();
        // NoteDestinationSettings.showSettings();
        // VelocityShapeSettings.showSettings();

        showSetting(PostActionSettings.allSettings);
        PostActionSettings.showPostActionsSettings();

        EditClipSettings.hideEditClipSettings();
    }

    public static void gotoMacroMode() {


        for (Setting setting : ProgramPattern.allSettings) {
            setting.hide();
        }

        for (Setting setting : MoveStepsHandler.allSettings) {
            setting.hide();
        }

        for (Setting setting : PatternSettings.allSettings) {
            setting.hide();
        }

        for (Setting setting : PostActionSettings.allSettings) {
            setting.hide();
        }

        for (Setting setting : NoteDestinationSettings.allSettings) {
            setting.hide();
        }

        for (Setting setting : StepSizeSettings.allSettings) {
            setting.hide();
        }

        for (Setting setting : ProgramPattern.allSettings) {
            setting.hide();
        }

        for (Setting setting : MoveStepsHandler.allSettings) {
            setting.hide();
        }

        for (Setting setting : PatternSettings.allSettings) {
            setting.hide();
        }

        for (Setting setting : PostActionSettings.allSettings) {
            setting.hide();
        }

        for (Setting setting : NoteDestinationSettings.allSettings) {
            setting.hide();
        }

        for (Setting setting : ProgramPattern.allSettings) {
            setting.hide();
        }

        for (Setting setting : ClipUtils.allSettings) {
            setting.hide();
        }

        for (Setting setting : MacroActionSettings.allSettings) {
            setting.show();
        }



        ProgramPattern.hideProgramPatternSettings();
        EditClipSettings.hideEditClipSettings();
    }

}
