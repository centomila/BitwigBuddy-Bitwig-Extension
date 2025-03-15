package com.centomila;

import static com.centomila.utils.PopupUtils.showPopup;
import static com.centomila.utils.SettingsHelper.*;

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
    // Static constants
    private static final String CATEGORY_GENERATE_PATTERN = "3 Generate Pattern";
    private static final String CATEGORY_CUSTOM_PATTERN_STRINGS = "Custom Pattern Strings";
    private static final String CATEGORY_CUSTOM_PATTERN_TOGGLE = "Custom Pattern Toggle";
    private static final String CATEGORY_CUSTOM_PATTERN_SAVE = "Custom Pattern Save";

    // Static state variables
    private static String lastDefaultPresetUsed = "Kick: Four on the Floor";
    private static String lastCustomPresetUsed = null;

    // UI Settings - Pattern Generation
    public static Setting spacerGenerate;
    public static Setting generateBtnSignalSetting;
    public static Setting patternTypeSetting;
    public static Setting patternSelectorSetting;
    public static Setting reversePatternSetting;
    public static Setting presetPatternStringSetting;

    // UI Settings - Custom Presets
    public static Setting customPresetSetting;
    public static Setting customRefreshPresetsSetting;
    
    public static Setting customPresetNoteDestinationSelectorSetting;


    // UI Settings - Custom Preset Toggles
    public static Setting customPresetStepSizeToggleSetting;
    public static Setting customPresetSubdivisionsToggleSetting;
    public static Setting customPresetNoteLengthToggleSetting;

    // UI Settings - Save Presets
    public static Setting customPresetSaveHeaderSetting;
    public static Setting customPresetSaveBtnSignal;
    public static Setting customPresetSaveNamSetting;

    // All settings collection
    public static Setting[] allSettings;

    // Instance variables
    private final BitwigBuddyExtension extension;
    private String lastStringPatternUsed = "100,0,0,0,100,0,0,0,100,0,0,0,100,0,0,0";

    // Constructor and main initialization methods
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
     */
    public static void init(BitwigBuddyExtension extension) {
        PatternSettings settings = new PatternSettings(extension);
        settings.initPatternSetting();
    }

    // Main initialization method
    private void initPatternSetting() {
        DocumentState documentState = extension.getDocumentState();
        initSpacer(documentState);
        initGenerateButton(documentState);
        initPatternTypeSetting(documentState);
        // initPatternSelectorSetting(documentState);

        initCustomPresetParams(documentState);
        initCustomPresetPatternStringSetting(documentState);
        initRefreshCustomPresetsSetting(documentState);
        initReversePatternSetting(documentState);

        initCustomPresetDefaultNoteToggleSetting(documentState);
        initCustomPresetStepSizeToggleSetting(documentState);
        initCustomPresetSubdivisionsToggleSetting(documentState);
        initCustomPresetNoteLengthToggleSetting(documentState);

        initCustomSavePresetSetting(documentState);
        allSettings = new Setting[] {
                spacerGenerate,
                generateBtnSignalSetting,
                patternTypeSetting,
                
                customPresetSetting,
                presetPatternStringSetting,
                customRefreshPresetsSetting,
                reversePatternSetting,
                
                customPresetStepSizeToggleSetting,
                customPresetSubdivisionsToggleSetting,
                customPresetNoteLengthToggleSetting,

                customPresetSaveBtnSignal,
                customPresetSaveNamSetting,
                customPresetSaveHeaderSetting
        };
    }

    // Base UI element initialization methods
    private void initSpacer(DocumentState documentState) {
        spacerGenerate = (Setting) createStringSetting(
                titleWithLine("GENERATE PATTERN"),
                CATEGORY_GENERATE_PATTERN, 0,
                "---------------------------------------------------");
        disableSetting(spacerGenerate); // Spacers are always disabled
    }

    private void initGenerateButton(DocumentState documentState) {
        generateBtnSignalSetting = (Setting) documentState.getSignalSetting("Generate!", CATEGORY_GENERATE_PATTERN,
                "Generate!");

        ((Signal) generateBtnSignalSetting).addSignalObserver(extension::generateDrumPattern);
    }

    public void initPatternTypeSetting(DocumentState documentState) {
        String[] options = { "Custom", "Program" };
        patternTypeSetting = (Setting) documentState.getEnumSetting("Pattern Type", CATEGORY_GENERATE_PATTERN,
                options, "Custom");

        ((EnumValue) patternTypeSetting).addValueObserver(newValue -> {
            generatorTypeSelector(newValue);
        });
    }

    private void initReversePatternSetting(DocumentState documentState) {
        reversePatternSetting = (Setting) createEnumSetting(
                "Reverse Pattern",
                CATEGORY_GENERATE_PATTERN,
                new String[] { "Normal", "Reverse" },
                "Normal");
    }

    // Custom preset initialization methods
    private void initCustomPresetParams(DocumentState documentState) {
        String[] presets = getCustomPresetsContentNameStrings();
        customPresetSetting = (Setting) documentState.getEnumSetting("Custom Presets",
                CATEGORY_GENERATE_PATTERN, presets,
                presets[0]);

        hideSetting(customPresetSetting);

        ((EnumValue) customPresetSetting).addValueObserver(newValue -> {
            if (!((EnumValue) patternTypeSetting).get().equals("Custom") || newValue == null) {
                return;
            }

            lastCustomPresetUsed = newValue.toString();
            if (lastCustomPresetUsed.equals("NO CUSTOM PRESETS")) {
                setPatternString("0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0");
                setDefaultNoteString("C1");
                setStepSizeString("1/16");
                setSubdivisionsString("Straight");
                setNoteLengthString("1/16");
                return;
            }

            String pattern = String.join(",", getCustomPresetsContentPatternStrings(newValue));
            showPopup("Custom Preset selected: " + newValue.toString());
            setPatternString(pattern);

            String defaultNote = getCustomPresetDefaultNote(newValue.toString());
            setDefaultNoteString(defaultNote.isEmpty() ? "C1" : defaultNote);
            if (((EnumValue) customPresetNoteDestinationSelectorSetting).get().equals("Preset Default Note")) {
                NoteDestinationSettings.setNoteAndOctaveFromString(defaultNote.isEmpty() ? "C1" : defaultNote);
            }

            String stepSize = getCustomPresetStepSize(newValue.toString());
            setStepSizeString(stepSize.isEmpty() ? "1/16" : stepSize);
            if (((EnumValue) customPresetStepSizeToggleSetting).get().equals("Enable") &&
                    stepSize != null && !stepSize.trim().isEmpty()) {
                ((SettableEnumValue) StepSizeSettings.stepSizSetting).set(stepSize);
            }

            String subdivisions = getCustomPresetSubdivisions(newValue.toString());
            setSubdivisionsString(subdivisions.isEmpty() ? "Straight" : subdivisions);
            if (((EnumValue) customPresetSubdivisionsToggleSetting).get().equals("Enable") &&
                    subdivisions != null && !subdivisions.trim().isEmpty()) {
                ((SettableEnumValue) StepSizeSettings.stepSizSubdivisionSetting).set(subdivisions);
            }

            String noteLength = getCustomPresetNoteLength(newValue.toString());
            setNoteLengthString(noteLength.isEmpty() ? "1/16" : noteLength);
            if (((EnumValue) customPresetNoteLengthToggleSetting).get().equals("Enable") &&
                    noteLength != null && !noteLength.trim().isEmpty()) {
                ((SettableEnumValue) StepSizeSettings.noteLengthSetting).set(noteLength);
            }
        });
    }

    private void initCustomPresetPatternStringSetting(DocumentState documentState) {
        presetPatternStringSetting = (Setting) documentState.getStringSetting("Steps",
                CATEGORY_GENERATE_PATTERN, 0,
                lastStringPatternUsed);
    }

    private void initRefreshCustomPresetsSetting(DocumentState documentState) {
        customRefreshPresetsSetting = (Setting) documentState.getSignalSetting("Refresh Custom Files",
                CATEGORY_GENERATE_PATTERN, "Refresh Custom Files");
        ((Signal) customRefreshPresetsSetting).addSignalObserver(() -> {

            extension.restart();
        });
    }

    private void initCustomSavePresetSetting(DocumentState documentState) {
        customPresetSaveHeaderSetting = (Setting) documentState.getStringSetting(
                titleWithLine("SAVE THIS PRESET"),
                CATEGORY_CUSTOM_PATTERN_SAVE, 0,
                "---------------------------------------------------");
        disableSetting(customPresetSaveHeaderSetting);

        customPresetSaveBtnSignal = (Setting) documentState.getSignalSetting("Save Custom Preset",
                CATEGORY_CUSTOM_PATTERN_SAVE, "Save Custom Preset");

        ((Signal) customPresetSaveBtnSignal).addSignalObserver(() -> {
            String presetName = ((EnumValue) customPresetSetting).get();
            String patternString = ((SettableStringValue) presetPatternStringSetting).get().trim();
            int[] patternIntArray = Arrays.stream(patternString.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .mapToInt(Integer::parseInt)
                    .toArray();
            String defaultNote = ((SettableStringValue) NoteDestinationSettings.customPresetDefaultNoteSetting).get();
            String stepSize = ((SettableEnumValue) StepSizeSettings.stepSizSetting).get();
            String subdivisions = ((SettableEnumValue) StepSizeSettings.stepSizSubdivisionSetting).get();
            String noteLength = ((SettableEnumValue) StepSizeSettings.noteLengthSetting).get();
            CustomPreset preset = new CustomPreset(presetName, presetName, defaultNote, patternIntArray, stepSize,
                    subdivisions, noteLength);
            CustomPresetsHandler.saveCustomPreset(preset, extension.preferences, extension.getHost());
            showPopup("Custom Preset saved: " + presetName);
            extension.restart();
        });

        customPresetSaveNamSetting = (Setting) documentState.getStringSetting("Preset Name",
                CATEGORY_CUSTOM_PATTERN_SAVE, 0,
                "New Custom Preset");
    }

    // Toggle initialization methods
    private void initCustomPresetDefaultNoteToggleSetting(DocumentState documentState) {
        customPresetNoteDestinationSelectorSetting = (Setting) createEnumSetting(
                "Note Preset/Custom",
                CATEGORY_CUSTOM_PATTERN_TOGGLE,
                new String[] { "Preset Default Note", "Custom" },
                "Preset Default Note");

        ((EnumValue) customPresetNoteDestinationSelectorSetting).addValueObserver(newValue -> {
            toggleCustomPresetNoteDestinationSelectorSetting();
        });
    }

    private void initCustomPresetStepSizeToggleSetting(DocumentState documentState) {
        customPresetStepSizeToggleSetting = (Setting) createEnumSetting(
                "Step Size Preset/Custom",
                CATEGORY_CUSTOM_PATTERN_TOGGLE,
                new String[] { "From Preset", "Custom" },
                "From Preset");

        ((EnumValue) customPresetStepSizeToggleSetting).addValueObserver(newValue -> {
            toggleCustomPresetStepSizeSetting();
        });
    }

    private void initCustomPresetSubdivisionsToggleSetting(DocumentState documentState) {
        customPresetSubdivisionsToggleSetting = (Setting) createEnumSetting(
                "Subdivisions Preset/Custom",
                CATEGORY_CUSTOM_PATTERN_TOGGLE,
                new String[] { "From Preset", "Custom" },
                "From Preset");

        ((EnumValue) customPresetSubdivisionsToggleSetting).addValueObserver(newValue -> {
            toggleCustomPresetSubdivisionsSetting();
        });
    }

    private void initCustomPresetNoteLengthToggleSetting(DocumentState documentState) {
        customPresetNoteLengthToggleSetting = (Setting) createEnumSetting(
                "Note Length Preset/Custom",
                CATEGORY_CUSTOM_PATTERN_TOGGLE,
                new String[] { "From Preset", "Custom" },
                "From Preset");

        ((EnumValue) customPresetNoteLengthToggleSetting).addValueObserver(newValue -> {
            toggleCustomPresetNoteLengthSetting();
        });
    }

    // Static utility methods for UI state management
    public static void generatorTypeSelector(String newValue) {
        switch (newValue) {
            case "Custom":
                Setting[] settingsToShowCustom = {
                        customPresetSetting,
                        customRefreshPresetsSetting,
                        reversePatternSetting,
                        customPresetNoteDestinationSelectorSetting, customPresetSaveBtnSignal,
                        customPresetSaveNamSetting,
                        customPresetNoteDestinationSelectorSetting, customPresetStepSizeToggleSetting,
                        customPresetSubdivisionsToggleSetting, customPresetNoteLengthToggleSetting };
                Setting[] settingsToHideCustom = {
                        // patternSelectorSetting,
                        ProgramPattern.programDensitySetting,
                        ProgramPattern.programMinVelocityVariationSetting,
                        ProgramPattern.programMaxVelocityVariationSetting,
                        ProgramPattern.programStepQtySetting,
                        ProgramPattern.programVelocitySettingShape };
                showSetting(settingsToShowCustom);
                hideSetting(settingsToHideCustom);

                if (lastCustomPresetUsed != null) {
                    try {
                        ((SettableEnumValue) customPresetSetting).set(lastCustomPresetUsed);
                    } catch (Exception e) {
                        showPopup("Custom Preset not found: " + lastCustomPresetUsed);
                        lastCustomPresetUsed = "NO CUSTOM PRESETS";
                        ((SettableEnumValue) customPresetSetting).set(lastCustomPresetUsed);
                    }
                }

                break;
            case "Program":
                Setting[] settingsToShowRandom = {
                        ProgramPattern.programDensitySetting,
                        ProgramPattern.programMinVelocityVariationSetting,
                        ProgramPattern.programMaxVelocityVariationSetting,
                        ProgramPattern.programStepQtySetting,
                        ProgramPattern.programVelocitySettingShape };
                Setting[] settingsToHideRandom = {
                        // patternSelectorSetting,
                        customPresetSetting,
                        reversePatternSetting,
                        customRefreshPresetsSetting, NoteDestinationSettings.customPresetDefaultNoteSetting,
                        customPresetNoteDestinationSelectorSetting, customPresetSaveBtnSignal,
                        customPresetSaveNamSetting,
                        StepSizeSettings.customPresetStepSizeSetting, StepSizeSettings.customPresetSubdivisionsSetting, StepSizeSettings.customPresetNoteLengthSetting,
                        customPresetNoteDestinationSelectorSetting,
                        customPresetStepSizeToggleSetting, customPresetSubdivisionsToggleSetting,
                        customPresetNoteLengthToggleSetting };
                showSetting(settingsToShowRandom);
                hideSetting(settingsToHideRandom);
                break;
        }
        // Reset the note destination setting for custom presets
        toggleCustomPresetNoteDestinationSelectorSetting();
    }

    public static void toggleCustomPresetNoteDestinationSelectorSetting() {
        String value = ((EnumValue) customPresetNoteDestinationSelectorSetting).get();
        String patternType = ((EnumValue) patternTypeSetting).get();
        // if (!patternType.equals("Custom")) {
        // enableSetting(NoteDestinationSettings.noteDestinationSetting);
        // enableSetting(NoteDestinationSettings.noteOctaveSetting);
        // hideSetting(customPresetDefaultNoteSetting);
        // return;
        // }
        if (value.equals("Preset Default Note")) {
            hideSetting(NoteDestinationSettings.noteDestinationSetting);
            hideSetting(NoteDestinationSettings.noteOctaveSetting);
            showSetting(NoteDestinationSettings.customPresetDefaultNoteSetting);

            hideSetting(NoteDestinationSettings.noteChannelSetting);

        } else {
            showSetting(NoteDestinationSettings.noteDestinationSetting);
            showSetting(NoteDestinationSettings.noteOctaveSetting);
            hideSetting(NoteDestinationSettings.customPresetDefaultNoteSetting);
            if (GlobalPreferences.showChannelDestinationPref.get()) {
                showSetting(NoteDestinationSettings.noteChannelSetting);
            }
        }

    }

    public static void toggleCustomPresetStepSizeSetting() {
        String value = ((EnumValue) customPresetStepSizeToggleSetting).get();
        if (value.equals("From Preset")) {
            showSetting(StepSizeSettings.customPresetStepSizeSetting);
        } else {
            hideSetting(StepSizeSettings.customPresetStepSizeSetting);
        }
    }

    public static void toggleCustomPresetSubdivisionsSetting() {
        String value = ((EnumValue) customPresetSubdivisionsToggleSetting).get();
        if (value.equals("From Preset")) {
            showSetting(StepSizeSettings.customPresetSubdivisionsSetting);
        } else {
            hideSetting(StepSizeSettings.customPresetSubdivisionsSetting);
        }
    }

    public static void toggleCustomPresetNoteLengthSetting() {
        String value = ((EnumValue) customPresetNoteLengthToggleSetting).get();
        if (value.equals("From Preset")) {
            showSetting(StepSizeSettings.customPresetNoteLengthSetting);
        } else {
            hideSetting(StepSizeSettings.customPresetNoteLengthSetting);
        }
    }

    // Data setter utility methods
    private static void setPatternString(String patternByName) {
        String patternType = ((EnumValue) patternTypeSetting).get();
        if (patternType.equals("Program")) {
            patternByName = new int[16].toString();
        } else {
            ((SettableStringValue) presetPatternStringSetting).set(patternByName);
        }
    }

    private static void setDefaultNoteString(String note) {
        ((SettableStringValue) NoteDestinationSettings.customPresetDefaultNoteSetting).set(note);
    }

    private static void setStepSizeString(String stepSize) {
        ((SettableStringValue) StepSizeSettings.customPresetStepSizeSetting).set(stepSize);
    }

    private static void setSubdivisionsString(String subdivisions) {
        ((SettableStringValue) StepSizeSettings.customPresetSubdivisionsSetting).set(subdivisions);
    }

    private static void setNoteLengthString(String noteLength) {
        ((SettableStringValue) StepSizeSettings.customPresetNoteLengthSetting).set(noteLength);
    }

    // Custom preset data retrieval methods
    private String[] getCustomPresetsContentNameStrings() {
        String[] presets = Arrays.stream(extension.preferences.getCustomPresets())
                .map(CustomPreset::getName)
                .sorted((a, b) -> Utils.naturalCompare(a, b))
                .toArray(String[]::new);
        return presets.length == 0 ? new String[] { "NO CUSTOM PRESETS" } : presets;
    }

    private String[] getCustomPresetsContentPatternStrings(String presetName) {
        CustomPresetsHandler handler = new CustomPresetsHandler(extension.getHost(), extension.preferences);
        int[] pattern = handler.getCustomPatternByName(presetName);
        if (pattern == null) {
            // Return a default empty pattern if the preset is not found
            showPopup("Preset not found: " + presetName + ". Using default pattern.");
            return new String[] { "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0" };
        }
        return Arrays.stream(pattern).mapToObj(String::valueOf).toArray(String[]::new);
    }


    private String getCustomPresetDefaultNote(String presetName) {
        for (CustomPreset preset : extension.preferences.getCustomPresets()) {
            if (preset.getName().equals(presetName)) {
                extension.getHost()
                        .println("Found preset: " + presetName + " with default note: " + preset.getDefaultNote()) ;
                return preset.getDefaultNote();
            }
        }

        return "";
    }

    private String getCustomPresetStepSize(String presetName) {
        for (CustomPreset preset : extension.preferences.getCustomPresets()) {
            if (preset.getName().equals(presetName)) {
                extension.getHost()
                        .println("Found preset: " + presetName + " with step size: " + preset.getStepSize()) ;
                return preset.getStepSize();
            }
        }
        return "";
    }

    private String getCustomPresetSubdivisions(String presetName) {
        for (CustomPreset preset : extension.preferences.getCustomPresets()) {
            if (preset.getName().equals(presetName)) {
                return preset.getSubdivisions();
            }
        }
        return "";
    }

    private String getCustomPresetNoteLength(String presetName) {
        for (CustomPreset preset : extension.preferences.getCustomPresets()) {
            if (preset.getName().equals(presetName)) {
                return preset.getNoteLength();
            }
        }
        return "";
    }
}