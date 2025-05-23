package com.centomila;

import static com.centomila.utils.SettingsHelper.*;

import com.bitwig.extension.controller.api.EnumValue;
import com.bitwig.extension.controller.api.Setting;

public class ModeSelectSettings {
    private static final String CATEGORY_MODE_SELECT = "1 Mode Select";
    public static final String MODE_GENERATE = "Generate";
    public static final String MODE_EDIT = "Edit";
    public static final String MODE_MACRO = "Macro";

    private static final String DESTINATION_LAUNCHER = "Launcher";
    private static final String DESTINATION_ARRANGER = "Arranger";

    public static Setting modeGenerateEditToggleSetting;
    public static Setting toggleLauncherArrangerSetting;

    public static void init(BitwigBuddyExtension extension) {
        // Mode select setting
        final String[] MODE_SELECT_OPTIONS = new String[] { MODE_GENERATE, MODE_EDIT, MODE_MACRO };

        // Initialize mode select settings
        initModeSelectSettings(MODE_SELECT_OPTIONS);

        // Set up observers
        initToggleModeObservers();
    }

    private static void initModeSelectSettings(String[] MODE_SELECT_OPTIONS) {
        // spacerSelectModSetting = (Setting) createStringSetting(
        //         titleWithLine("MODE SELECT ------------------------------"),
        //         CATEGORY_MODE_SELECT, 0,
        //         "---------------------------------------------------");
        // disableSetting(spacerSelectModSetting);

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
    }

    public static void hideMacroSettings() {
        MacroActionSettings.showMacroSlots(false, false, false, false);
        MacroActionSettings.hideInstantMacro();
        hideSetting(MacroActionSettings.allSettings);
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
        
        ProgramPattern.hideAndDisableAllSettings();
        PatternSettings.hideAllSettings();
        MoveStepsHandler.hideAllSettings();
        CustomPresetSaver.hideAllSettings();
        PostActionSettings.hideAllSettings();
        StepSizeSettings.hideAllSettings();
        NoteDestinationSettings.hideAllSettings();

        MacroActionSettings.showMacroSlots(false, false, false, false);
        MacroActionSettings.hideAllSettings(); // Hide all macro-related settings
        
        // Show edit settings
        ProgramPattern.showAndEnableAllSettings(); // Show all
        ProgramPattern.programStepQtySetting.hide(); // Hide step qty setting
        ProgramPattern.programDensitySetting.hide(); // Hide density setting
        ProgramPattern.programSkipStep.hide(); // Hide skip step setting

        ClipUtils.showAllSettings();
        EditClipSettings.showAllSettings();
    }

    // GENERATE MODE
    public static void gotoGenerateMode() {
        ProgramPattern.hideAndDisableAllSettings();

        // get the value from GlobalPreferences of showChannelDestination
        if (!GlobalPreferences.showChannelDestinationPref.get()) {
            hideSetting(NoteDestinationSettings.noteChannelSetting);
        }

        // Hide all macro-related settings
        // First, hide all macro-related settings before showing generate settings
        MacroActionSettings.hideAllSettings();
        MacroActionSettings.showMacroSlots(false, false, false, false);
        MacroActionSettings.hideInstantMacro();
        MacroActionSettings.macroHeaderSetting.hide();
        MacroActionSettings.macroViewSelectorSetting.hide();

        EditClipSettings.hideAllSettings();

        // Show generate settings
        PatternSettings.showAllSettings();
        NoteDestinationSettings.showAllSettings();
        StepSizeSettings.showAllSettings();
        MoveStepsHandler.showAllSettings();
        ClipUtils.showAllSettings();
        showSetting(PostActionSettings.allSettings);
        
        
        
        PostActionSettings.toggleVisibilityPostActionsSettings();
        PatternSettings.generatorTypeSelector(PatternSettings.getPatternType());

    }

    public static void gotoMacroMode() {
        // Hide all other settings first

        hideSetting(ProgramPattern.allSettings);
        hideSetting(MoveStepsHandler.allSettings);
        hideSetting(PatternSettings.allSettings);
        hideSetting(PostActionSettings.allSettings);
        hideSetting(NoteDestinationSettings.allSettings);
        hideSetting(StepSizeSettings.allSettings);
        hideSetting(ClipUtils.allSettings);
        hideSetting(CustomPresetSaver.allSettings);

        ProgramPattern.hideAndDisableAllSettings();
        EditClipSettings.hideAllSettings();

        // Show macro header and view selector
        MacroActionSettings.macroHeaderSetting.show();
        MacroActionSettings.macroViewSelectorSetting.show();

        // Let the view selector's observer handle which macro slots to show
        String currentView = ((EnumValue) MacroActionSettings.macroViewSelectorSetting).get();
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

    /**
     * @return the current mode as a string
     */
    public static String getCurrentMode() {
        return ((EnumValue) modeGenerateEditToggleSetting).get();
    }

    /**
     * @return the current launcher/arranger toggle setting as a string
     */
    public static String getCurrentLauncherArrangerToggleString() {
        return ((EnumValue) toggleLauncherArrangerSetting).get();
    }

}
