package com.centomila;

import com.bitwig.extension.controller.api.DocumentState;
import com.bitwig.extension.controller.api.EnumValue;
import com.bitwig.extension.controller.api.Setting;
import com.bitwig.extension.controller.api.Signal;
import com.centomila.BeatBuddyPreferences.CustomPreset;
import java.util.Arrays;

/**
 * Manages the pattern settings for the BeatBuddy extension including presets,
 * random, and custom patterns.
 */
public class PatternSettings {
    private final BeatBuddyExtension extension;

    /**
     * Constructs a new instance of PatternSettings.
     *
     * @param extension The BeatBuddyExtension instance.
     */
    public PatternSettings(BeatBuddyExtension extension) {
        this.extension = extension;
    }

    /**
     * Creates a PatternSettings instance and initializes all pattern-related settings.
     *
     * @param extension The BeatBuddyExtension instance.
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
        initGenerateButton(documentState);
        initPatternTypeSetting(documentState);
        initPatternSelectorSetting(documentState);
        initCustomPresetSetting(documentState);
        initReversePatternSetting(documentState);
        initSpacer(documentState);
    }

    /**
     * Initializes the generate button setting and binds it to the drum pattern generation.
     *
     * @param documentState The current document state.
     */
    private void initGenerateButton(DocumentState documentState) {
        Signal generateButton = documentState.getSignalSetting("Generate!", "Generate", "Generate!");
        generateButton.addSignalObserver(extension::generateDrumPattern);
    }

    /**
     * Initializes the pattern type setting and sets up observers to show/hide related settings.
     *
     * @param documentState The current document state.
     */
    private void initPatternTypeSetting(DocumentState documentState) {
        String[] options = { "Presets", "Random", "Custom" };
        extension.setPatternTypeSetting((Setting) documentState.getEnumSetting("Pattern Type", "Generate", options, "Presets"));

        ((EnumValue) extension.getPatternTypeSetting()).addValueObserver(newValue -> {
            switch (newValue) {
                case "Presets":
                    showPatternSetting();
                    hideCustomPresetSetting();
                    showReversePatternSetting();
                    break;
                case "Custom":
                    hidePatternSetting();
                    showCustomPresetSetting();
                    showReversePatternSetting();
                    break;
                case "Random":
                    hidePatternSetting();
                    hideCustomPresetSetting();
                    hideReversePatternSetting();
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
        final String[] PATTERN_OPTIONS = Arrays.stream(DrumPatterns.patterns)
                .map(pattern -> pattern[0].toString())
                .toArray(String[]::new);

        extension.setPatternSelectorSetting(
                (Setting) documentState.getEnumSetting("Pattern", "Generate", PATTERN_OPTIONS, "Kick: Four on the Floor"));
        ((EnumValue) extension.getPatternSelectorSetting()).addValueObserver(newValue -> {
            PopupUtils.showPopup(newValue.toString());
        });
    }

    /**
     * Initializes the custom preset setting and adds an observer for selection events.
     *
     * @param documentState The current document state.
     */
    private void initCustomPresetSetting(DocumentState documentState) {
        String[] presets = getCustomPresetsContentNameStrings();
        extension.setCustomPresetSetting((Setting) documentState.getEnumSetting("Custom Presets", "Generate", presets, presets[0]));
        extension.getCustomPresetSetting().disable(); // Disabled initially until "Custom" is selected.
        ((EnumValue) extension.getCustomPresetSetting()).addValueObserver(newValue -> {
            PopupUtils.showPopup("Custom Preset selected: " + newValue.toString());
        });
    }

    /**
     * Initializes the reverse pattern setting.
     *
     * @param documentState The current document state.
     */
    private void initReversePatternSetting(DocumentState documentState) {
        extension.setReversePatternSetting((Setting) documentState.getEnumSetting("Reverse Pattern", "Generate",
                new String[] { "Normal", "Reverse" }, "Normal"));
    }

    /**
     * Initializes the spacer setting to visually separate groups of controls.
     *
     * @param documentState The current document state.
     */
    private void initSpacer(DocumentState documentState) {
        Setting spacer = (Setting) documentState.getStringSetting("----", "Generate", 0,
                "---------------------------------------------------");
        spacer.disable();
        extension.setSpacer1(spacer);
    }

    /**
     * Retrieves an array of custom preset names.
     *
     * @return An array containing the names of the custom presets.
     */
    private String[] getCustomPresetsContentNameStrings() {
        return Arrays.stream(extension.getPreferences().getCustomPresets())
                     .map(CustomPreset::getName)
                     .toArray(String[]::new);
    }

    /**
     * Enables and shows the pattern selector setting.
     */
    private void showPatternSetting() {
        extension.getPatternSelectorSetting().enable();
        extension.getPatternSelectorSetting().show();
    }

    /**
     * Disables and hides the pattern selector setting.
     */
    private void hidePatternSetting() {
        extension.getPatternSelectorSetting().disable();
        extension.getPatternSelectorSetting().hide();
    }

    /**
     * Enables and shows the custom preset setting.
     */
    private void showCustomPresetSetting() {
        extension.getCustomPresetSetting().enable();
        extension.getCustomPresetSetting().show();
    }

    /**
     * Disables and hides the custom preset setting.
     */
    private void hideCustomPresetSetting() {
        extension.getCustomPresetSetting().disable();
        extension.getCustomPresetSetting().hide();
    }

    /**
     * Enables and shows the reverse pattern setting.
     */
    private void showReversePatternSetting() {
        extension.getReversePatternSetting().enable();
        extension.getReversePatternSetting().show();
    }

    /**
     * Disables and hides the reverse pattern setting.
     */
    private void hideReversePatternSetting() {
        extension.getReversePatternSetting().disable();
        extension.getReversePatternSetting().hide();
    }
}