package com.centomila;

import static com.centomila.utils.PopupUtils.showPopup;
import static com.centomila.utils.SettingsHelper.*;

import com.bitwig.extension.controller.api.DocumentState;
import com.bitwig.extension.controller.api.Setting;
import com.bitwig.extension.controller.api.Signal;
import com.bitwig.extension.controller.api.SettableStringValue;
import com.centomila.CustomPresetsHandler.CustomPreset;
import com.centomila.utils.SettingsHelper;

import java.util.Arrays;

/**
 * Class dedicated to handling the custom preset saving functionality
 */
public class CustomPresetSaver {
    // Static constants
    public static final String CATEGORY_CUSTOM_PATTERN_SAVE = "99 Custom Pattern Save";

    // UI Settings - Save Presets
    public static Setting customPresetSaveHeaderSetting;
    public static Setting customPresetSaveBtnSignal;
    public static Setting customPresetSaveNameSetting;
    public static Setting customRefreshPresetsSetting; // Added refresh setting
    public static Setting[] allSettings;

    // Constructor and main initialization methods
    @SuppressWarnings("unused")
    private final BitwigBuddyExtension extension;

    /**
     * Constructs a new instance of CustomPresetSaver.
     *
     * @param extension The BitwigBuddyExtension instance.
     */
    public CustomPresetSaver(BitwigBuddyExtension extension) {
        this.extension = extension;
    }

    /**
     * Initializes the custom preset save settings
     * 
     * @param documentState The document state to create settings in
     * @param extension The BitwigBuddyExtension instance
     * @return Array of settings that were created
     */
    public static Setting[] initCustomSavePresetSetting(DocumentState documentState, BitwigBuddyExtension extension) {
        customPresetSaveHeaderSetting = (Setting) SettingsHelper.createStringSetting(
                SettingsHelper.titleWithLine("SAVE THIS PRESET -------------------------"),
                CATEGORY_CUSTOM_PATTERN_SAVE, 0,
                "---------------------------------------------------");

        disableSetting(customPresetSaveHeaderSetting);

        customPresetSaveBtnSignal = (Setting) documentState.getSignalSetting("Save Custom Preset",
                CATEGORY_CUSTOM_PATTERN_SAVE, "Save Custom Preset");

        ((Signal) customPresetSaveBtnSignal).addSignalObserver(() -> {
            String presetName = ((SettableStringValue) customPresetSaveNameSetting).get();
            String patternString = ((SettableStringValue) PatternSettings.presetPatternStringSetting).get().trim();
            int[] patternIntArray = Arrays.stream(patternString.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .mapToInt(Integer::parseInt)
                    .toArray();
                    
            // Always use current note value
            String defaultNote = NoteDestinationSettings.getCurrentNoteAsString();
            
            // Always use current step size
            String stepSize = StepSizeSettings.getStepSize();
            
            // Always use current subdivisions and note length
            String subdivisions = StepSizeSettings.getSubdivisions();
            String noteLength = StepSizeSettings.getNoteLength();
            
            CustomPreset preset = new CustomPreset(presetName, presetName, defaultNote, patternIntArray, stepSize,
                    subdivisions, noteLength);
            CustomPresetsHandler.saveCustomPreset(preset, extension.preferences, extension.getHost());
            
            // wait half second to allow the preset to be saved
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            showPopup("Custom Preset saved: " + presetName);
            extension.restart();
        });

        customPresetSaveNameSetting = (Setting) documentState.getStringSetting("Preset Name",
                CATEGORY_CUSTOM_PATTERN_SAVE, 0,
                "New Custom Preset");
        
        // Add refresh custom presets setting
        customRefreshPresetsSetting = (Setting) documentState.getSignalSetting("Refresh Custom Files",
                CATEGORY_CUSTOM_PATTERN_SAVE, "Refresh Custom Files");
        ((Signal) customRefreshPresetsSetting).addSignalObserver(() -> {
            extension.restart();
        });
                
        // Apply initial visibility based on current pattern type
        String patternType = PatternSettings.getPatternType();
        if (patternType != null) {
            if (patternType.equals("Custom")) {
                SettingsHelper.showSetting(new Setting[] {
                    customPresetSaveHeaderSetting,
                    customPresetSaveBtnSignal,
                    customPresetSaveNameSetting,
                    customRefreshPresetsSetting
                });
            } else if (patternType.equals("Program")) {
                SettingsHelper.showSetting(new Setting[] {
                    customPresetSaveHeaderSetting,
                    customPresetSaveBtnSignal,
                    customPresetSaveNameSetting,
                    customRefreshPresetsSetting
                });
            }
        }

        allSettings = new Setting[] {
            customPresetSaveHeaderSetting,
            customPresetSaveBtnSignal,
            customPresetSaveNameSetting,
            customRefreshPresetsSetting
        };
                
        return new Setting[] {
            customPresetSaveHeaderSetting,
            customPresetSaveBtnSignal,
            customPresetSaveNameSetting,
            customRefreshPresetsSetting
        };
    }
    
    /**
     * Gets the save preset header setting
     */
    public static Setting getCustomPresetSaveHeaderSetting() {
        return customPresetSaveHeaderSetting;
    }
    
    /**
     * Gets the save preset button signal setting
     */
    public static Setting getCustomPresetSaveBtnSignal() {
        return customPresetSaveBtnSignal;
    }
    
    /**
     * Gets the save preset name setting
     */
    public static Setting getCustomPresetSaveNameSetting() {
        return customPresetSaveNameSetting;
    }

    /**
     * Gets the refresh presets setting
     */
    public static Setting getCustomRefreshPresetsSetting() {
        return customRefreshPresetsSetting;
    }

    // Show / Hide settings
    public static void showAllSettings() {
        showSetting(allSettings);
    }

    public static void hideAllSettings() {
        hideSetting(allSettings);
    }

    public static void hideAndDisableAllSettings() {
        hideAndDisableSetting(allSettings);
    }

    public static void showAndEnableAllSettings() {
        showAndEnableSetting(allSettings);
        disableSetting(customPresetSaveHeaderSetting);
    }
}
