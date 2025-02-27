package com.centomila;

import static com.centomila.utils.PopupUtils.showPopup;
import static com.centomila.utils.SettingsHelper.disableSetting;
import static com.centomila.utils.SettingsHelper.hideAndDisableSetting;
import static com.centomila.utils.SettingsHelper.showAndEnableSetting;

import java.util.Arrays;
import java.util.stream.Collectors;

import com.bitwig.extension.controller.api.DocumentState;
import com.bitwig.extension.controller.api.EnumValue;
import com.bitwig.extension.controller.api.SettableStringValue;
import com.bitwig.extension.controller.api.SettableEnumValue;
import com.bitwig.extension.controller.api.Setting;
import com.bitwig.extension.controller.api.Signal;
import com.centomila.CustomPresetsHandler.CustomPreset;

/**
 * Manages the pattern settings for the BitwigBuddy extension including presets,
 * random, and custom patterns.
 */
public class PatternSettings {
       // Pattern settings
    public static Setting patternTypeSetting; // Pattern Type "Preset", "Random", "Custom"
    public static Setting patternSelectorSetting; // List of default patterns
    public static Setting customPresetSetting; // List of custom patterns
    public static Setting presetPatternStringSetting; // Custom pattern string
    public static Setting reversePatternSetting;
    private final BitwigBuddyExtension extension;
    private static String CATEGORY_GENERATE_PATTERN = "Generate Pattern";
    private String lastDefaultPresetUsed = "Kick: Four on the Floor";
    private String lastCustomPresetUsed = null;
    private String lastStringPatternUsed = "100,0,0,0,100,0,0,0,100,0,0,0,100,0,0,0";

    /**
     * Constructs a new instance of PatternSettings.
     *
     * @param extension The BitwigBuddyExtension instance.
     */
    public PatternSettings(BitwigBuddyExtension extension) {
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
     * @param extension The BitwigBuddyExtension instance to configure settings for
     */
    public static void init(BitwigBuddyExtension extension) {
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
        patternTypeSetting = (Setting) documentState.getEnumSetting("Pattern Type", CATEGORY_GENERATE_PATTERN,
                options, "Presets");

        ((EnumValue) patternTypeSetting).addValueObserver(newValue -> {
            switch (newValue) {
                case "Presets":
                    Setting[] settingsToShow = {
                            patternSelectorSetting,
                            reversePatternSetting };
                    Setting[] settingsToHide = {
                            customPresetSetting,
                            RandomPattern.randomDensitySetting,
                            RandomPattern.randomMinVelocityVariationSetting,
                            RandomPattern.randomMaxVelocityVariationSetting,
                            RandomPattern.randomStepQtySetting,
                            RandomPattern.randomVelocitySettingShape };
                    showAndEnableSetting(settingsToShow);
                    hideAndDisableSetting(settingsToHide);

                    ((SettableEnumValue) patternSelectorSetting).set(lastDefaultPresetUsed);
                    setPatternString(getDefaultPresetsContentPatternStrings(lastDefaultPresetUsed));
                    break;
                case "Custom":
                    Setting[] settingsToShowCustom = {
                            customPresetSetting,
                            reversePatternSetting };
                    Setting[] settingsToHideCustom = {
                            patternSelectorSetting,
                            RandomPattern.randomDensitySetting,
                            RandomPattern.randomMinVelocityVariationSetting,
                            RandomPattern.randomMaxVelocityVariationSetting,
                            RandomPattern.randomStepQtySetting,
                            RandomPattern.randomVelocitySettingShape };
                    showAndEnableSetting(settingsToShowCustom);
                    hideAndDisableSetting(settingsToHideCustom);

                    if (lastCustomPresetUsed != null) {
                        ((SettableEnumValue) customPresetSetting).set(lastCustomPresetUsed);
                    }

                    break;
                case "Random":
                    Setting[] settingsToShowRandom = { RandomPattern.randomDensitySetting,
                            RandomPattern.randomMinVelocityVariationSetting,
                            RandomPattern.randomMaxVelocityVariationSetting, RandomPattern.randomStepQtySetting,
                            RandomPattern.randomVelocitySettingShape };
                    Setting[] settingsToHideRandom = { patternSelectorSetting, customPresetSetting,
                            reversePatternSetting };
                    showAndEnableSetting(settingsToShowRandom);
                    hideAndDisableSetting(settingsToHideRandom);
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
        final String[] LIST_OF_DEFAULT_PATTERNS = Arrays.stream(DefaultPatterns.patterns)
                .map(pattern -> pattern[0].toString())
                .toArray(String[]::new);

        patternSelectorSetting = (Setting) documentState.getEnumSetting("Pattern", CATEGORY_GENERATE_PATTERN,
                LIST_OF_DEFAULT_PATTERNS,
                "Kick: Four on the Floor");

        ((EnumValue) patternSelectorSetting).addValueObserver(newValue -> {
            if (!((EnumValue) patternTypeSetting).get().equals("Presets")) {
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
        customPresetSetting = (Setting) documentState.getEnumSetting("Custom Presets",
                CATEGORY_GENERATE_PATTERN, presets,
                presets[0]);

        hideAndDisableSetting(customPresetSetting);

        ((EnumValue) customPresetSetting).addValueObserver(newValue -> {
            // if preset type
            if (!((EnumValue) patternTypeSetting).get().equals("Custom")) {
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
        String patternType = ((EnumValue) patternTypeSetting).get();
        if (patternType.equals("Random")) {
            patternByName = new int[16].toString();
        } else {
            ((SettableStringValue) presetPatternStringSetting).set(patternByName);
        }
    }

    private void initCustomPresetPatternSetting(DocumentState documentState) {
        presetPatternStringSetting = (Setting) documentState.getStringSetting("Steps",
                CATEGORY_GENERATE_PATTERN, 0,
                lastStringPatternUsed);
    }

    /**
     * Initializes the reverse pattern setting.
     *
     * @param documentState The current document state.
     */
    private void initReversePatternSetting(DocumentState documentState) {
        reversePatternSetting = (Setting) documentState.getEnumSetting("Reverse Pattern",
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