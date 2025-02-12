package com.centomila;

import com.bitwig.extension.controller.api.DocumentState;
import com.bitwig.extension.controller.api.EnumValue;
import com.bitwig.extension.controller.api.Setting;
import com.bitwig.extension.controller.api.Signal;

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

        // Generate button
        Signal generateButton = documentState.getSignalSetting("Generate!", "Generate", "Generate!");

        generateButton.addSignalObserver(() -> {
            extension.generateDrumPattern();
        });

        // Pattern type selector
        extension.setPatternTypeSetting((Setting) documentState.getEnumSetting("Pattern Type", "Generate",
                new String[] { "Presets", "Random", "Custom" }, "Presets"));

        ((EnumValue) extension.getPatternTypeSetting()).addValueObserver(newValue -> {
            switch (newValue) {
                case "Presets":
                    extension.getPatternSelectorSetting().enable();
                    extension.getPatternSelectorSetting().show();
                    extension.getCustomPresetSetting().disable();
                    extension.getCustomPresetSetting().hide();
                    extension.getReversePatternSetting().enable();
                    extension.getReversePatternSetting().show();
                    break;
                case "Custom":
                    extension.getCustomPresetSetting().enable();
                    extension.getCustomPresetSetting().show();
                    extension.getPatternSelectorSetting().disable();
                    extension.getPatternSelectorSetting().hide();
                    extension.getReversePatternSetting().enable();
                    extension.getReversePatternSetting().show();
                    break;
                case "Random":
                    extension.getPatternSelectorSetting().disable();
                    extension.getPatternSelectorSetting().hide();
                    extension.getCustomPresetSetting().disable();
                    extension.getCustomPresetSetting().hide();
                    extension.getReversePatternSetting().disable();
                    extension.getReversePatternSetting().hide();
                    break;
            }
        });

        // Define pattern settings
        final String[] PATTERN_OPTIONS = Arrays.stream(DrumPatterns.patterns)
                .map(pattern -> pattern[0].toString())
                .toArray(String[]::new);
        extension.setPatternSelectorSetting((Setting) documentState.getEnumSetting("Pattern", "Generate", PATTERN_OPTIONS,
                "Kick: Four on the Floor"));
        ((EnumValue) extension.getPatternSelectorSetting()).addValueObserver(newValue -> {
            PopupUtils.showPopup(newValue.toString());
        });

        // New Custom Presets dropdown (for Custom)
        extension.setCustomPresetSetting((Setting) documentState.getEnumSetting("Custom Presets", "Generate",
                new String[] { "TO BE IMPLEMENTED" }, "TO BE IMPLEMENTED"));
        extension.getCustomPresetSetting().disable();
        ((EnumValue) extension.getCustomPresetSetting()).addValueObserver(newValue -> {
            PopupUtils.showPopup("Custom Preset selected: " + newValue.toString());
        });

        extension.setReversePatternSetting((Setting) documentState.getEnumSetting("Reverse Pattern", "Generate",
                new String[] { "Normal", "Reverse" }, "Normal"));

        // Empty string for spacing
        extension.setSpacer1((Setting) documentState.getStringSetting("----", "Generate", 0,
                "---------------------------------------------------"));
        extension.getSpacer1().disable();
    }

    public void generateDrumPattern() {
    }
}