package com.centomila;

import com.bitwig.extension.controller.api.DocumentState;
import com.bitwig.extension.controller.api.EnumValue;
import com.bitwig.extension.controller.api.SettableEnumValue;
import com.bitwig.extension.controller.api.SettableStringValue;
import com.bitwig.extension.controller.api.Setting;
import com.bitwig.extension.controller.api.Signal;
import com.centomila.CustomPresetsHandler.CustomPreset;
import static com.centomila.utils.PopupUtils.*;
import static com.centomila.utils.SettingsHelper.*;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Manages the pattern settings for the BeatBuddy extension including presets,
 * random, and custom patterns.
 */
public class PatternSettings {
    private final BeatBuddyExtension extension;
    private static String CATEGORY_GENERATE_PATTERN = "Generate Pattern";
    private String lastDefaultPresetUsed = "Kick: Four on the Floor";
    private String lastCustomPresetUsed = null;

    /**
     * Constructs a new instance of PatternSettings.
     *
     * @param extension The BeatBuddyExtension instance.
     */
    public PatternSettings(BeatBuddyExtension extension) {
        this.extension = extension;
    }

    /**
     * Creates and initializes pattern-related settings for the extension.
     * Sets up UI controls including:
     * - Generate button
     * - Pattern type selector (Presets/Random/Custom)
     * - Pattern preset selector
     * - Custom preset selector
     * - Pattern reversal option
     * 
     * @param extension The BeatBuddyExtension instance to configure settings for
     */
    public static void init(BeatBuddyExtension extension) {
        PatternSettings settings = new PatternSettings(extension);
        settings.initPatternSetting();
    }

    /**
     * Initializes all pattern-related settings.
     */
    private void initPatternSetting() {
        DocumentState documentState = extension.getDocumentState();
        initSpacer(documentState);
        initGenerateButton(documentState);
        initPatternTypeSetting(documentState);
        initPatternSelectorSetting(documentState);
        initCustomPresetSetting(documentState);
        initCustomPresetPatternSetting(documentState);
        initReversePatternSetting(documentState);
    }

    /**
     * Initializes the generate button setting and binds it to the drum pattern
     * generation.
     *
     * @param documentState The current document state.
     */
    private void initGenerateButton(DocumentState documentState) {
        Signal generateButton = documentState.getSignalSetting("Generate!", CATEGORY_GENERATE_PATTERN, "Generate!");
        generateButton.addSignalObserver(extension::generateDrumPattern);
    }

    /**
     * Initializes the pattern type setting and sets up observers to show/hide
     * related settings.
     *
     * @param documentState The current document state.
     */
    private void initPatternTypeSetting(DocumentState documentState) {
        String[] options = { "Presets", "Random", "Custom" };
        extension.patternTypeSetting = (Setting) documentState.getEnumSetting("Pattern Type", CATEGORY_GENERATE_PATTERN,
                options, "Presets");
        

        ((EnumValue) extension.patternTypeSetting).addValueObserver(newValue -> {
            switch (newValue) {
                case "Presets":
                    Setting[] settingsToShow = { extension.patternSelectorSetting, extension.reversePatternSetting };
                    Setting[] settingsToHide = { extension.customPresetSetting };                
                    showAndEnableSetting(settingsToShow);
                    hideAndDisalbeSetting(settingsToHide);

                    
                    ((SettableEnumValue) extension.patternSelectorSetting).set(lastDefaultPresetUsed);
                    setPatternString(getDefaultPresetsContentPatternStrings(lastDefaultPresetUsed));
                    break;
                case "Custom":
                    Setting[] settingsToShowCustom = { extension.customPresetSetting, extension.reversePatternSetting };
                    Setting[] settingsToHideCustom = { extension.patternSelectorSetting };
                    showAndEnableSetting(settingsToShowCustom);
                    hideAndDisalbeSetting(settingsToHideCustom);
                
                        if (lastCustomPresetUsed != null) {
                            ((SettableEnumValue) extension.customPresetSetting).set(lastCustomPresetUsed);
                        }
                
                    break;
                case "Random":
                    // Setting[] settingsToShowRandom = {  }; TODO: ADD RANDOM SETTINGS
                    Setting[] settingsToHideRandom = { extension.patternSelectorSetting, extension.customPresetSetting, 
                            extension.reversePatternSetting };
                    // showAndEnableSetting(settingsToShowRandom);
                    hideAndDisalbeSetting(settingsToHideRandom);
                    break;
            }
        });
    }

    /**
     * Initializes the pattern selector setting.
     *
     * @param documentState The current document state.
     */
    private void initPatternSelectorSetting(DocumentState documentState) {
        final String[] PATTERN_OPTIONS = Arrays.stream(DefaultPatterns.patterns)
                .map(pattern -> pattern[0].toString())
                .toArray(String[]::new);

        extension.patternSelectorSetting = (Setting) documentState.getEnumSetting("Pattern", CATEGORY_GENERATE_PATTERN,
                PATTERN_OPTIONS,
                "Kick: Four on the Floor");
                

        ((EnumValue) extension.patternSelectorSetting).addValueObserver(newValue -> {
            if (!((EnumValue) extension.patternTypeSetting).get().equals("Presets")) {
                return;
            }
            lastDefaultPresetUsed = newValue.toString();
            showPopup(newValue.toString());
            String patternByName = getDefaultPresetsContentPatternStrings(newValue);
            setPatternString(patternByName);
        });
    }

    private String getDefaultPresetsContentPatternStrings(String newValue) {
        String patternByName = Arrays.stream(DefaultPatterns.getPatternByName(newValue.toString()))
                .mapToObj(String::valueOf)
                .collect(Collectors.joining(","));
        return patternByName;
    }

    /**
     * Initializes the custom preset setting and adds an observer for selection
     * events.
     *
     * @param documentState The current document state.
     */
    private void initCustomPresetSetting(DocumentState documentState) {
        String[] presets = getCustomPresetsContentNameStrings();
        extension.customPresetSetting = (Setting) documentState.getEnumSetting("Custom Presets",
                CATEGORY_GENERATE_PATTERN, presets,
                presets[0]);

        hideAndDisalbeSetting(null, extension.customPresetSetting);

        ((EnumValue) extension.customPresetSetting).addValueObserver(newValue -> {
            // if preset type 
            if (!((EnumValue) extension.patternTypeSetting).get().equals("Custom")) {
                return;
            }
            if (newValue != null) {
                lastCustomPresetUsed = newValue.toString();
            } else {
                lastCustomPresetUsed = presets[0];
            }
            String pattern = String.join(",", getCustomPresetsContentPatternStrings(newValue));
            showPopup("Custom Preset selected: " + newValue.toString() + " with pattern: " + pattern);
            // convert pattern to Setting
            setPatternString(pattern);
        });
    }

    private void setPatternString(String patternByName) {
        String patternType = ((EnumValue) extension.patternTypeSetting).get();
        if (patternType.equals("Random")) {
            patternByName = new int[16].toString();
        } else {
            ((SettableStringValue) extension.presetPatternStringSetting).set(patternByName);
        }
    }

    private void initCustomPresetPatternSetting(DocumentState documentState) {
        // String[] presets = getCustomPresetsContentNameStrings();
        extension.presetPatternStringSetting = (Setting) documentState.getStringSetting("Steps",
                CATEGORY_GENERATE_PATTERN, 0,
                "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0");
    }

    /**
     * Initializes the reverse pattern setting.
     *
     * @param documentState The current document state.
     */
    private void initReversePatternSetting(DocumentState documentState) {
        extension.reversePatternSetting = (Setting) documentState.getEnumSetting("Reverse Pattern",
                CATEGORY_GENERATE_PATTERN,
                new String[] { "Normal", "Reverse" }, "Normal");
    }

    /**
     * Initializes the spacer setting to visually separate groups of controls.
     *
     * @param documentState The current document state.
     */
    private void initSpacer(DocumentState documentState) {
        Setting spacerGenerate = (Setting) documentState.getStringSetting("PATTERN-----------------------------",
                CATEGORY_GENERATE_PATTERN, 0,
                "---------------------------------------------------");
        disableSetting(spacerGenerate); // Spacers are always disabled
    }

    /**
     * Retrieves an array of custom preset names.
     *
     * @return An array containing the names of the custom presets.
     */
    private String[] getCustomPresetsContentNameStrings() {
        return Arrays.stream(extension.preferences.getCustomPresets())
                .map(CustomPreset::getName)
                .sorted((a, b) -> Utils.naturalCompare(a, b))
                .toArray(String[]::new);
    }

    private String[] getCustomPresetsContentPatternStrings(String presetName) {
        CustomPresetsHandler handler = new CustomPresetsHandler(extension.getHost(), extension.preferences);
        int[] pattern = handler.getCustomPatternByName(presetName);
        return Arrays.stream(pattern).mapToObj(String::valueOf).toArray(String[]::new);
    }

}