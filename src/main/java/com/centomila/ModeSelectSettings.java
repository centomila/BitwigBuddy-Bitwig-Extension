package com.centomila;

import static com.centomila.utils.SettingsHelper.*;

import java.util.List;

import com.bitwig.extension.controller.api.EnumValue;
import com.bitwig.extension.controller.api.Setting;

public class ModeSelectSettings {
    private static final String CATEGORY_MODE_SELECT = "1 Mode Select";
    public static final String MODE_GENERATE = "Generate";
    public static final String MODE_EDIT = "Edit";
    public static final String MODE_MACRO = "Macro";

    private static final String DESTINATION_LAUNCHER = "Launcher";
    private static final String DESTINATION_ARRANGER = "Arranger";

    private static Setting spacerSelectModSetting;
    public static Setting modeGenerateEditToggleSetting;
    public static Setting toggleLauncherArrangerSetting;

    public static void init(BitwigBuddyExtension extension) {
        // Mode select setting
        final String[] MODE_SELECT_OPTIONS = new String[] { MODE_GENERATE, MODE_EDIT, MODE_MACRO };

        spacerSelectModSetting = (Setting) createStringSetting(titleWithLine("MODE SELECT"),
                CATEGORY_MODE_SELECT, 0,
                "---------------------------------------------------");
        disableSetting(spacerSelectModSetting);

        modeGenerateEditToggleSetting = (Setting) createEnumSetting("Generate/Edit Mode", CATEGORY_MODE_SELECT,
                MODE_SELECT_OPTIONS,
                MODE_SELECT_OPTIONS[0]);

        // Launcher/Arranger toggle
        final String[] TOGGLE_LAUNCHER_ARRANGER_OPTIONS = new String[] {
                DESTINATION_LAUNCHER, DESTINATION_ARRANGER
        };

        toggleLauncherArrangerSetting = (Setting) createEnumSetting(
                "Destination Launcher/Arranger",
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
            if (newValue.equals(DESTINATION_ARRANGER)) {
                disableSetting(PostActionSettings.duplicateClipSetting);
            } else {
                enableSetting(PostActionSettings.duplicateClipSetting);
            }
        });
    }

    private static void toggleMode(String newValue) {
        if (MODE_GENERATE.equals(newValue)) {
            gotoGenerateMode();
        } else if (MODE_EDIT.equals(newValue)) {
            gotoEditMode();
        } else if (MODE_MACRO.equals(newValue)) {
            gotoMacroMode();
        }
    }

    private static void gotoEditMode() {
        // Hide generate settings
        // RandomPattern.hideSettings();
        // for each Setting in RandomPattern.allSettings, hideSetting(setting);

        for (Setting setting : ProgramPattern.allSettings) {
            hideSetting(setting);
        }

        for (Setting setting : MoveStepsHandler.allSettings) {
            hideSetting(setting);
        }

        for (Setting setting : PatternSettings.allSettings) {
            hideSetting(setting);
        }

        for (Setting setting : PostActionSettings.allSettings) {
            hideSetting(setting);
        }

        for (Setting setting : NoteDestinationSettings.allSettings) {
            hideSetting(setting);
        }

        for (Setting setting : StepSizeSettings.allSettings) {
            hideSetting(setting);
        }

        ProgramPattern.showProgramPatternSettings();

        EditClipSettings.showEditClipSettings();

        NoteDestinationSettings.noteDestinationSetting.hide();
        NoteDestinationSettings.noteChannelSetting.hide();
        ProgramPattern.programStepQtySetting.hide();
        ProgramPattern.programDensitySetting.hide();

        MacroActionSettings.hideMacroSettings();

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

        showSetting(PostActionSettings.allSettings);
        PostActionSettings.showPostActionsSettings();

        // Hide all macro-related settings
        MacroActionSettings.hideMacroSettings();
        MacroActionSettings.showMacroSlots(false, false, false, false);
        MacroActionSettings.hideInstantMacro();
        MacroActionSettings.macroHeaderSetting.hide();
        MacroActionSettings.macroViewSelectorSetting.hide();

        EditClipSettings.hideEditClipSettings();
    }

    public static void gotoMacroMode() {
        // Hide all other settings first
        for (Setting setting : ProgramPattern.allSettings) {
            hideSetting(setting);
        }

        for (Setting setting : MoveStepsHandler.allSettings) {
            hideSetting(setting);
        }

        for (Setting setting : PatternSettings.allSettings) {
            if (setting == PatternSettings.customRefreshPresetsSetting) {
                showAndEnableSetting(setting);
            } else {
                hideSetting(setting);
            }
        }

        for (Setting setting : PostActionSettings.allSettings) {
            hideSetting(setting);
        }

        for (Setting setting : NoteDestinationSettings.allSettings) {
            hideSetting(setting);
        }

        for (Setting setting : StepSizeSettings.allSettings) {
            hideSetting(setting);
        }

        for (Setting setting : ClipUtils.allSettings) {
            hideSetting(setting);
        }

        ProgramPattern.hideProgramPatternSettings();
        EditClipSettings.hideEditClipSettings();
        
        // Show macro header and view selector
        MacroActionSettings.macroHeaderSetting.show();
        MacroActionSettings.macroViewSelectorSetting.show();
        
        // Let the view selector's observer handle which macro slots to show
        String currentView = ((EnumValue)MacroActionSettings.macroViewSelectorSetting).get();
        switch (currentView) {
            case MacroActionSettings.VIEW_ALL:
                MacroActionSettings.showMacroSlots(true, true, true, true);
                MacroActionSettings.hideInstantMacro();
                break;
            case MacroActionSettings.VIEW_SLOT1:
                MacroActionSettings.showMacroSlots(true, false, false, false);
                MacroActionSettings.hideInstantMacro();
                break;
            case MacroActionSettings.VIEW_SLOT2:
                MacroActionSettings.showMacroSlots(false, true, false, false);
                MacroActionSettings.hideInstantMacro();
                break;
            case MacroActionSettings.VIEW_SLOT3:
                MacroActionSettings.showMacroSlots(false, false, true, false);
                MacroActionSettings.hideInstantMacro();
                break;
            case MacroActionSettings.VIEW_SLOT4:
                MacroActionSettings.showMacroSlots(false, false, false, true);
                MacroActionSettings.hideInstantMacro();
                break;
            case MacroActionSettings.VIEW_IM:
                MacroActionSettings.showMacroSlots(false, false, false, false);
                MacroActionSettings.showInstantMacro();
                break;
            case MacroActionSettings.VIEW_ALL_IM:
                MacroActionSettings.showMacroSlots(true, true, true, true);
                MacroActionSettings.showInstantMacro();
                break;
        }
    }

}
