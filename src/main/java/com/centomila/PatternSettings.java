package com.centomila;

import com.bitwig.extension.controller.api.DocumentState;
import com.bitwig.extension.controller.api.EnumValue;
import com.bitwig.extension.controller.api.Setting;
import com.bitwig.extension.controller.api.Signal;
import com.centomila.BeatBuddyPreferences.CustomPreset;
import java.util.Arrays;

public class PatternSettings {
    private final BeatBuddyExtension extension;

    public PatternSettings(BeatBuddyExtension extension) {
        this.extension = extension;
    }

    public static void init(BeatBuddyExtension extension) {
        PatternSettings settings = new PatternSettings(extension);
        settings.initPatternSetting();
    }

    private void initPatternSetting() {
        DocumentState documentState = extension.getDocumentState();
        initGenerateButton(documentState);
        initPatternTypeSetting(documentState);
        initPatternSelectorSetting(documentState);
        initCustomPresetSetting(documentState);
        initReversePatternSetting(documentState);
        initSpacer(documentState);
    }

    private void initGenerateButton(DocumentState documentState) {
        Signal generateButton = documentState.getSignalSetting("Generate!", "Generate", "Generate!");
        generateButton.addSignalObserver(extension::generateDrumPattern);
    }

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

    private void initCustomPresetSetting(DocumentState documentState) {
        String[] presets = getCustomPresetsContentNameStrings();
        extension.setCustomPresetSetting((Setting) documentState.getEnumSetting("Custom Presets", "Generate", presets, presets[0]));
        extension.getCustomPresetSetting().disable();
        ((EnumValue) extension.getCustomPresetSetting()).addValueObserver(newValue -> {
            PopupUtils.showPopup("Custom Preset selected: " + newValue.toString());
        });
    }

    private void initReversePatternSetting(DocumentState documentState) {
        extension.setReversePatternSetting((Setting) documentState.getEnumSetting("Reverse Pattern", "Generate", 
                new String[] { "Normal", "Reverse" }, "Normal"));
    }

    private void initSpacer(DocumentState documentState) {
        Setting spacer = (Setting) documentState.getStringSetting("----", "Generate", 0,
                "---------------------------------------------------");
        spacer.disable();
        extension.setSpacer1(spacer);
    }

    // Helper methods for showing/hiding settings
    private void showPatternSetting() {
        extension.getPatternSelectorSetting().enable();
        extension.getPatternSelectorSetting().show();
    }

    private void hidePatternSetting() {
        extension.getPatternSelectorSetting().disable();
        extension.getPatternSelectorSetting().hide();
    }

    private void showCustomPresetSetting() {
        extension.getCustomPresetSetting().enable();
        extension.getCustomPresetSetting().show();
    }

    private void hideCustomPresetSetting() {
        extension.getCustomPresetSetting().disable();
        extension.getCustomPresetSetting().hide();
    }

    private void showReversePatternSetting() {
        extension.getReversePatternSetting().enable();
        extension.getReversePatternSetting().show();
    }

    private void hideReversePatternSetting() {
        extension.getReversePatternSetting().disable();
        extension.getReversePatternSetting().hide();
    }

    private String[] getCustomPresetsContentNameStrings() {
        return Arrays.stream(extension.getPreferences().getCustomPresets())
                     .map(CustomPreset::getName)
                     .toArray(String[]::new);
    }
}